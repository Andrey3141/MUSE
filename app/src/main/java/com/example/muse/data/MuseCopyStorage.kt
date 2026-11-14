package com.example.muse.data

import android.content.Context
import java.io.File
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.StandardOpenOption
import java.util.UUID

object MuseCopyStorage {

    fun isValidTrackId(trackId: String): Boolean {
        if (trackId.isEmpty()) return false
        if (trackId.contains('/') || trackId.contains('\\')) return false
        if (trackId == "." || trackId == "..") return false
        return true
    }

    private fun baseDir(context: Context): File =
        File(context.filesDir, "muse_copies")

    sealed class DirResult {
        data class Success(val dir: File) : DirResult()
        data class Failed(val cause: Throwable) : DirResult()
    }

    private fun ensureDir(context: Context): DirResult {
        val d = baseDir(context)
        if (d.exists() && d.isDirectory) return DirResult.Success(d)
        return try {
            if (d.mkdirs() && d.isDirectory) DirResult.Success(d)
            else DirResult.Failed(IllegalStateException("mkdirs failed or not a directory"))
        } catch (e: Exception) {
            DirResult.Failed(e)
        }
    }

    fun finalFile(context: Context, trackId: String): File =
        File(baseDir(context), trackId)

    fun isExpectedFinalFile(context: Context, trackId: String, file: File): Boolean {
        if (!isValidTrackId(trackId)) return false
        return try {
            val base = baseDir(context).canonicalPath
            val target = file.canonicalPath
            if (!target.startsWith("$base${File.separator}")) return false
            file.name == trackId
        } catch (e: Exception) {
            false
        }
    }

    sealed class LockResult {
        data class Success(val channel: FileChannel, val lock: FileLock) : LockResult()
        data class Failed(val cause: Throwable) : LockResult()
    }

    /**
     * Захватывает эксклюзивный файловый лок для trackId.
     * Файл лока: filesDir/muse_copies/{trackId}.lock
     * Блокирует до получения лока или исключения.
     */
    fun acquireFileLock(context: Context, trackId: String): LockResult {
        if (!isValidTrackId(trackId)) {
            return LockResult.Failed(IllegalArgumentException("Invalid trackId: $trackId"))
        }
        return when (val dirResult = ensureDir(context)) {
            is DirResult.Success -> {
                try {
                    val lockFile = File(dirResult.dir, "$trackId.lock")
                    val channel = FileChannel.open(
                        lockFile.toPath(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE
                    )
                    val lock = channel.lock()
                    LockResult.Success(channel, lock)
                } catch (e: Exception) {
                    LockResult.Failed(e)
                }
            }
            is DirResult.Failed -> LockResult.Failed(dirResult.cause)
        }
    }

    fun releaseFileLock(channel: FileChannel, lock: FileLock) {
        try {
            lock.release()
        } catch (e: Exception) { }
        try {
            channel.close()
        } catch (e: Exception) { }
    }

    sealed class TmpFileResult {
        data class Success(val file: File) : TmpFileResult()
        data class DirFailed(val cause: Throwable) : TmpFileResult()
        data class CreateFailed(val cause: Throwable) : TmpFileResult()
        data class InvalidTrackId(val trackId: String) : TmpFileResult()
    }

    fun newTmpFile(context: Context, trackId: String): TmpFileResult {
        if (!isValidTrackId(trackId)) return TmpFileResult.InvalidTrackId(trackId)
        return when (val dirResult = ensureDir(context)) {
            is DirResult.Success -> {
                val f = File(dirResult.dir, "$trackId.${UUID.randomUUID()}.tmp")
                try {
                    if (f.createNewFile()) TmpFileResult.Success(f)
                    else TmpFileResult.CreateFailed(IllegalStateException("createNewFile returned false"))
                } catch (e: Exception) {
                    TmpFileResult.CreateFailed(e)
                }
            }
            is DirResult.Failed -> TmpFileResult.DirFailed(dirResult.cause)
        }
    }

    sealed class CleanupResult {
        data object Success : CleanupResult()
        data class DirUnreadable(val cause: Throwable?) : CleanupResult()
        data class PartialFailure(val failedFiles: List<String>, val cause: Throwable?) : CleanupResult()
    }

    private fun extractTrackIdFromTmp(fileName: String): String? {
        if (!fileName.endsWith(".tmp")) return null
        val withoutSuffix = fileName.removeSuffix(".tmp")
        val lastDot = withoutSuffix.lastIndexOf('.')
        if (lastDot <= 0) return null
        return withoutSuffix.substring(0, lastDot)
    }

    fun cleanupTmpFiles(context: Context, trackId: String): CleanupResult {
        if (!isValidTrackId(trackId)) return CleanupResult.Success
        val d = baseDir(context)
        if (!d.exists()) return CleanupResult.Success
        if (!d.isDirectory) return CleanupResult.DirUnreadable(IllegalStateException("Not a directory"))
        val files = try {
            d.listFiles()
        } catch (e: Exception) {
            return CleanupResult.DirUnreadable(e)
        } ?: return CleanupResult.DirUnreadable(null)
        val failed = mutableListOf<String>()
        var firstCause: Throwable? = null
        files.forEach { f ->
            if (extractTrackIdFromTmp(f.name) == trackId) {
                try {
                    if (!f.delete()) {
                        failed.add(f.name)
                        if (firstCause == null) firstCause = IllegalStateException("delete returned false")
                    }
                } catch (e: Exception) {
                    failed.add(f.name)
                    if (firstCause == null) firstCause = e
                }
            }
        }
        return if (failed.isEmpty()) CleanupResult.Success
        else CleanupResult.PartialFailure(failed, firstCause)
    }

    sealed class DeleteResult {
        data object Success : DeleteResult()
        data object NotFound : DeleteResult()
        data class Failed(val cause: Throwable?) : DeleteResult()
    }

    fun deleteFile(file: File): DeleteResult {
        if (!file.exists()) return DeleteResult.NotFound
        return try {
            if (file.delete()) DeleteResult.Success
            else DeleteResult.Failed(IllegalStateException("delete returned false"))
        } catch (e: Exception) {
            DeleteResult.Failed(e)
        }
    }

    fun deleteFinalFile(context: Context, trackId: String): DeleteResult {
        if (!isValidTrackId(trackId)) return DeleteResult.Failed(IllegalArgumentException("Invalid trackId"))
        return deleteFile(finalFile(context, trackId))
    }
}