package com.example.muse.data

import android.content.Context
import android.net.Uri
import android.os.StatFs
import android.provider.OpenableColumns
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.muse.core.database.MuseDatabase
import com.example.muse.core.database.TrackDao
import com.example.muse.core.model.Track
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.channels.FileLock

class CopyAllWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val processedInThisRun = mutableSetOf<String>()
    private var hadRetryableFailure = false

    override suspend fun doWork(): Result {
        val db = MuseDatabase.get(applicationContext)
        val dao = db.trackDao()

        try {
            reconcileCopyState(dao)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }

        while (!isStopped) {
            val track = try {
                findNextPendingTrack(dao)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                return Result.retry()
            } ?: break
            processTrack(dao, track)
        }

        val nearestRetryable = try {
            dao.findNearestRetryableAttemptAt(System.currentTimeMillis(), PERMANENT_THRESHOLD)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }

        return when {
            isStopped -> Result.success()
            hadRetryableFailure -> Result.retry()
            nearestRetryable != null -> {
                val delay = (nearestRetryable - System.currentTimeMillis()).coerceAtLeast(1L)
                BackupScheduler.scheduleDelayedCopy(applicationContext, delay)
                Result.success()
            }
            else -> Result.success()
        }
    }

    private suspend fun reconcileCopyState(dao: TrackDao) {
        val all = dao.getAll()
        for (entity in all) {
            if (!MuseCopyStorage.isValidTrackId(entity.id)) {
                safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.FAILED, null, PERMANENT_THRESHOLD)
                continue
            }

            when (entity.copyStatus) {
                Track.CopyStatus.IN_PROGRESS -> {
                    reconcileWithClaim(dao, entity) { finalFile ->
                        if (!MuseCopyStorage.isExpectedFinalFile(applicationContext, entity.id, finalFile)) return@reconcileWithClaim
                        when (MuseCopyStorage.cleanupTmpFiles(applicationContext, entity.id)) {
                            is MuseCopyStorage.CleanupResult.Success -> { }
                            else -> { hadRetryableFailure = true; return@reconcileWithClaim }
                        }
                        when (val result = verifyCopy(finalFile, entity.uri)) {
                            is CopyVerification.Verified ->
                                safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.COMPLETED, finalFile.absolutePath, 0L)
                            is CopyVerification.Unverified ->
                                safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.COMPLETED_UNVERIFIED, finalFile.absolutePath, 0L)
                            is CopyVerification.Invalid -> {
                                when (MuseCopyStorage.deleteFinalFile(applicationContext, entity.id)) {
                                    is MuseCopyStorage.DeleteResult.Success,
                                    is MuseCopyStorage.DeleteResult.NotFound ->
                                        safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.NONE, null, 0L)
                                    is MuseCopyStorage.DeleteResult.Failed -> hadRetryableFailure = true
                                }
                            }
                            is CopyVerification.PermissionError, is CopyVerification.RetryableError -> hadRetryableFailure = true
                        }
                    }
                }
                Track.CopyStatus.COMPLETED -> {
                    val path = entity.museCopyPath
                    if (path == null) {
                        safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.NONE, null, 0L)
                    } else {
                        val f = File(path)
                        if (!MuseCopyStorage.isExpectedFinalFile(applicationContext, entity.id, f)) continue
                        reconcileWithClaim(dao, entity) {
                            when (verifyCopy(f, entity.uri)) {
                                is CopyVerification.Verified -> { }
                                is CopyVerification.Unverified ->
                                    safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.COMPLETED_UNVERIFIED, path, 0L)
                                is CopyVerification.Invalid -> {
                                    when (MuseCopyStorage.deleteFile(f)) {
                                        is MuseCopyStorage.DeleteResult.Success,
                                        is MuseCopyStorage.DeleteResult.NotFound ->
                                            safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.NONE, null, 0L)
                                        is MuseCopyStorage.DeleteResult.Failed -> hadRetryableFailure = true
                                    }
                                }
                                is CopyVerification.PermissionError, is CopyVerification.RetryableError -> hadRetryableFailure = true
                            }
                        }
                    }
                }
                Track.CopyStatus.COMPLETED_UNVERIFIED -> {
                    val path = entity.museCopyPath
                    if (path == null) {
                        safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.NONE, null, 0L)
                    } else {
                        val f = File(path)
                        if (!MuseCopyStorage.isExpectedFinalFile(applicationContext, entity.id, f)) continue
                        reconcileWithClaim(dao, entity) {
                            when (verifyCopy(f, entity.uri)) {
                                is CopyVerification.Verified ->
                                    safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.COMPLETED, path, 0L)
                                is CopyVerification.Invalid -> {
                                    when (MuseCopyStorage.deleteFile(f)) {
                                        is MuseCopyStorage.DeleteResult.Success,
                                        is MuseCopyStorage.DeleteResult.NotFound ->
                                            safeUpdateIfOwner(dao, entity.id, entity.claimEpoch, Track.CopyStatus.NONE, null, 0L)
                                        is MuseCopyStorage.DeleteResult.Failed -> hadRetryableFailure = true
                                    }
                                }
                                is CopyVerification.PermissionError, is CopyVerification.RetryableError -> hadRetryableFailure = true
                                is CopyVerification.Unverified -> { }
                            }
                        }
                    }
                }
                Track.CopyStatus.FAILED -> {
                    reconcileWithClaim(dao, entity) {
                        when (MuseCopyStorage.cleanupTmpFiles(applicationContext, entity.id)) {
                            is MuseCopyStorage.CleanupResult.Success -> { }
                            else -> hadRetryableFailure = true
                        }
                    }
                }
            }
        }
    }

    /**
     * Выполняет reconciliation под claim. Если claim не получен — пропускает запись.
     */
    private suspend fun reconcileWithClaim(
        dao: TrackDao,
        entity: com.example.muse.core.database.entity.TrackEntity,
        block: suspend (File) -> Unit
    ) {
        val now = System.currentTimeMillis()
        val staleThreshold = now - STALE_CLAIM_MS
        val changed = try {
            dao.tryIncrementClaimEpoch(entity.id, now, staleThreshold)
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { hadRetryableFailure = true; return }

        if (changed == 0) return

        val epoch = try {
            dao.getCurrentClaimEpoch(entity.id) ?: return
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { hadRetryableFailure = true; return }

        val lockResult = MuseCopyStorage.acquireFileLock(applicationContext, entity.id)
        if (lockResult !is MuseCopyStorage.LockResult.Success) {
            hadRetryableFailure = true
            safeUpdateIfOwner(dao, entity.id, epoch, Track.CopyStatus.FAILED, null, retryableBackoffTime())
            return
        }

        try {
            val finalFile = MuseCopyStorage.finalFile(applicationContext, entity.id)
            block(finalFile)
        } finally {
            MuseCopyStorage.releaseFileLock(lockResult.channel, lockResult.lock)
            try {
                withContext(NonCancellable) { dao.releaseClaim(entity.id, epoch) }
            } catch (e: Exception) { }
        }
    }

    private fun verifyCopy(file: File, originalUri: String): CopyVerification {
        if (!file.exists() || file.length() == 0L) return CopyVerification.Invalid
        return when (val sizeResult = queryOriginalSize(Uri.parse(originalUri))) {
            is CopyVerification.Verified -> {
                if (file.length() == sizeResult.size) CopyVerification.Verified(sizeResult.size)
                else CopyVerification.Invalid
            }
            is CopyVerification.Unverified -> CopyVerification.Unverified
            is CopyVerification.Invalid -> CopyVerification.Invalid
            is CopyVerification.PermissionError -> sizeResult
            is CopyVerification.RetryableError -> sizeResult
        }
    }

    private suspend fun findNextPendingTrack(dao: TrackDao): Track? {
        val now = System.currentTimeMillis()
        val exclude = processedInThisRun.toList().ifEmpty { listOf("__none__") }
        val entity = dao.findNextPendingCopy(now, PERMANENT_THRESHOLD, now - STALE_CLAIM_MS, exclude) ?: return null
        return entity.toDomain()
    }

    private suspend fun processTrack(dao: TrackDao, track: Track) {
        val trackId = track.id
        if (!MuseCopyStorage.isValidTrackId(trackId)) return

        val now = System.currentTimeMillis()
        val staleThreshold = now - STALE_CLAIM_MS

        val changed = try {
            dao.tryIncrementClaimEpoch(trackId, now, staleThreshold)
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { hadRetryableFailure = true; return }

        if (changed == 0) return

        val epoch = try {
            dao.getCurrentClaimEpoch(trackId) ?: return
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { hadRetryableFailure = true; return }

        val lockResult = MuseCopyStorage.acquireFileLock(applicationContext, trackId)
        if (lockResult !is MuseCopyStorage.LockResult.Success) {
            hadRetryableFailure = true
            safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, retryableBackoffTime())
            try { withContext(NonCancellable) { dao.releaseClaim(trackId, epoch) } } catch (e: Exception) { }
            return
        }

        processedInThisRun.add(trackId)

        try {
            processTrackInternal(dao, track, epoch)
        } finally {
            MuseCopyStorage.releaseFileLock(lockResult.channel, lockResult.lock)
            try {
                withContext(NonCancellable) { dao.releaseClaim(trackId, epoch) }
            } catch (e: Exception) { }
        }
    }

    private suspend fun processTrackInternal(dao: TrackDao, track: Track, epoch: Long) {
        val trackId = track.id

        if (track.copyStatus == Track.CopyStatus.COMPLETED && track.museCopyPath != null) {
            val copyFile = File(track.museCopyPath)
            if (!MuseCopyStorage.isExpectedFinalFile(applicationContext, trackId, copyFile)) {
                hadRetryableFailure = true; return
            }
            when (verifyCopy(copyFile, track.uri)) {
                is CopyVerification.Verified -> return
                is CopyVerification.Unverified -> {
                    safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.COMPLETED_UNVERIFIED, track.museCopyPath, 0L)
                    return
                }
                is CopyVerification.Invalid -> {
                    when (MuseCopyStorage.deleteFile(copyFile)) {
                        is MuseCopyStorage.DeleteResult.Success,
                        is MuseCopyStorage.DeleteResult.NotFound ->
                            safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.NONE, null, 0L)
                        is MuseCopyStorage.DeleteResult.Failed -> { hadRetryableFailure = true; return }
                    }
                }
                is CopyVerification.PermissionError, is CopyVerification.RetryableError -> {
                    hadRetryableFailure = true; return
                }
            }
        }

        if (!track.originalAvailable || track.uri.isBlank()) {
            safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, PERMANENT_THRESHOLD)
            return
        }

        val originalUri = Uri.parse(track.uri)
        val sizeResult = queryOriginalSize(originalUri)

        when (sizeResult) {
            is CopyVerification.PermissionError -> {
                hadRetryableFailure = true
                safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, securityBackoffTime())
                return
            }
            is CopyVerification.RetryableError -> {
                hadRetryableFailure = true
                safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, retryableBackoffTime())
                return
            }
            else -> { }
        }

        val expectedSize: Long? = when (sizeResult) {
            is CopyVerification.Verified -> sizeResult.size
            else -> null
        }

        val copyDir = MuseCopyStorage.finalFile(applicationContext, trackId).parentFile
        if (copyDir?.mkdirs() == false && !copyDir.exists()) {
            hadRetryableFailure = true
            safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, retryableBackoffTime())
            return
        }

        if (expectedSize != null) {
            val available = try {
                StatFs(copyDir?.absolutePath ?: applicationContext.filesDir.absolutePath).availableBytes
            } catch (e: Exception) {
                hadRetryableFailure = true
                safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, retryableBackoffTime())
                return
            }
            if (expectedSize > Long.MAX_VALUE - SPACE_MARGIN_BYTES) {
                safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, PERMANENT_THRESHOLD)
                return
            }
            if (available < expectedSize + SPACE_MARGIN_BYTES) {
                hadRetryableFailure = true
                safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, retryableBackoffTime())
                return
            }
        }

        when (MuseCopyStorage.deleteFinalFile(applicationContext, trackId)) {
            is MuseCopyStorage.DeleteResult.Success,
            is MuseCopyStorage.DeleteResult.NotFound -> { }
            is MuseCopyStorage.DeleteResult.Failed -> {
                hadRetryableFailure = true
                safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, retryableBackoffTime())
                return
            }
        }

        if (!safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.IN_PROGRESS, null, 0L)) {
            hadRetryableFailure = true
            return
        }

        val tmpResult = MuseCopyStorage.newTmpFile(applicationContext, trackId)
        if (tmpResult !is MuseCopyStorage.TmpFileResult.Success) {
            hadRetryableFailure = true
            safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, retryableBackoffTime())
            return
        }
        val tmp = tmpResult.file
        val finalFile = MuseCopyStorage.finalFile(applicationContext, trackId)
        var renamed = false

        try {
            applicationContext.contentResolver.openInputStream(originalUri)
                ?.use { input ->
                    tmp.outputStream().use { output ->
                        val buffer = ByteArray(COPY_BUFFER_SIZE)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            if (isStopped) throw CancellationException("Worker stopped")
                            output.write(buffer, 0, read)
                        }
                    }
                } ?: run {
                handleCopyError(tmp, trackId, epoch, dao, retryableBackoffTime())
                return
            }

            if (tmp.length() == 0L) { handleCopyError(tmp, trackId, epoch, dao, retryableBackoffTime()); return }
            if (expectedSize != null && tmp.length() != expectedSize) { handleCopyError(tmp, trackId, epoch, dao, retryableBackoffTime()); return }

            if (!tmp.renameTo(finalFile)) { handleCopyError(tmp, trackId, epoch, dao, retryableBackoffTime()); return }
            renamed = true

            val status = if (expectedSize != null) Track.CopyStatus.COMPLETED else Track.CopyStatus.COMPLETED_UNVERIFIED
            safeUpdateIfOwner(dao, trackId, epoch, status, finalFile.absolutePath, 0L)
        } catch (e: CancellationException) {
            withContext(NonCancellable) {
                try {
                    if (renamed && finalFile.exists() && finalFile.length() > 0L) {
                        val expected = (queryOriginalSize(originalUri) as? CopyVerification.Verified)?.size
                        val status = if (expected != null && finalFile.length() == expected) Track.CopyStatus.COMPLETED
                        else Track.CopyStatus.COMPLETED_UNVERIFIED
                        try { dao.updateCopyStateIfOwner(trackId, epoch, status, finalFile.absolutePath, 0L) } catch (db: Exception) { }
                    } else {
                        when (MuseCopyStorage.deleteFile(tmp)) {
                            is MuseCopyStorage.DeleteResult.Success,
                            is MuseCopyStorage.DeleteResult.NotFound -> {
                                try { dao.updateCopyStateIfOwner(trackId, epoch, Track.CopyStatus.NONE, null, 0L) } catch (db: Exception) { }
                            }
                            is MuseCopyStorage.DeleteResult.Failed -> hadRetryableFailure = true
                        }
                    }
                } catch (cleanup: Exception) { }
            }
            throw e
        } catch (e: IOException) {
            if (!renamed) handleCopyError(tmp, trackId, epoch, dao, retryableBackoffTime()) else hadRetryableFailure = true
        } catch (e: SecurityException) {
            if (!renamed) handleCopyError(tmp, trackId, epoch, dao, securityBackoffTime()) else hadRetryableFailure = true
        } catch (e: Exception) {
            if (!renamed) handleCopyError(tmp, trackId, epoch, dao, retryableBackoffTime()) else hadRetryableFailure = true
        }
    }

    private suspend fun safeUpdateIfOwner(dao: TrackDao, trackId: String, epoch: Long, status: String, path: String?, nextAttemptAt: Long): Boolean {
        return try {
            val changed = withContext(NonCancellable) { dao.updateCopyStateIfOwner(trackId, epoch, status, path, nextAttemptAt) }
            changed == 1
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { hadRetryableFailure = true; false }
    }

    private suspend fun handleCopyError(tmp: File, trackId: String, epoch: Long, dao: TrackDao, backoff: Long) {
        when (MuseCopyStorage.deleteFile(tmp)) {
            is MuseCopyStorage.DeleteResult.Success,
            is MuseCopyStorage.DeleteResult.NotFound -> { }
            is MuseCopyStorage.DeleteResult.Failed -> hadRetryableFailure = true
        }
        hadRetryableFailure = true
        safeUpdateIfOwner(dao, trackId, epoch, Track.CopyStatus.FAILED, null, backoff)
    }

    private fun retryableBackoffTime(): Long = System.currentTimeMillis() + RETRYABLE_BACKOFF_MS
    private fun securityBackoffTime(): Long = System.currentTimeMillis() + SECURITY_BACKOFF_MS

    private fun queryOriginalSize(uri: Uri): CopyVerification {
        val cursor = try {
            applicationContext.contentResolver.query(uri, null, null, null, null)
        } catch (e: SecurityException) { return CopyVerification.PermissionError(e) }
        catch (e: CancellationException) { throw e }
        catch (e: Exception) { return CopyVerification.RetryableError(e) }

        if (cursor == null) return CopyVerification.RetryableError(IllegalStateException("query returned null"))

        return try {
            cursor.use {
                if (!it.moveToFirst()) return CopyVerification.RetryableError(IllegalStateException("empty cursor"))
                val idx = it.getColumnIndex(OpenableColumns.SIZE)
                if (idx < 0 || it.isNull(idx)) return CopyVerification.Unverified
                val size = it.getLong(idx)
                if (size >= 0) CopyVerification.Verified(size)
                else CopyVerification.RetryableError(IllegalStateException("negative size"))
            }
        } catch (e: CancellationException) { throw e }
        catch (e: Exception) { CopyVerification.RetryableError(e) }
    }

    companion object {
        private const val COPY_BUFFER_SIZE = 64 * 1024
        private const val SPACE_MARGIN_BYTES = 16L * 1024 * 1024
        private const val RETRYABLE_BACKOFF_MS = 20_000L
        private const val SECURITY_BACKOFF_MS = 30 * 60 * 1000L
        private const val PERMANENT_THRESHOLD = Long.MAX_VALUE
        private const val STALE_CLAIM_MS = 10 * 60 * 1000L
    }
}