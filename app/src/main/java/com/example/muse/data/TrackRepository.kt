package com.example.muse.data

import android.content.Context
import com.example.muse.core.database.MuseDatabase
import com.example.muse.core.database.entity.PlayEventEntity
import com.example.muse.core.database.entity.TrackEntity
import com.example.muse.core.media.MediaStoreScanner
import com.example.muse.core.model.PlayEvent
import com.example.muse.core.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ScanResult(
    val newCount: Int,
    val scannedStableKeys: Set<String>,
    val isComplete: Boolean,
    val errorMessage: String? = null
)

class TrackRepository(
    private val context: Context,
    private val database: MuseDatabase
) {

    suspend fun scanAndMerge(): ScanResult = withContext(Dispatchers.IO) {
        val outcome = MediaStoreScanner.scanAudio(context)

        if (!outcome.isComplete) {
            return@withContext ScanResult(0, emptySet(), isComplete = false, errorMessage = outcome.errorMessage)
        }

        var newCount = 0
        for (candidate in outcome.tracks) {
            val existing = database.trackDao().findByStableKey(candidate.stableKey)
            if (existing == null) {
                val entity = TrackEntity.fromDomain(
                    candidate.copy(museCopyPath = null, copyStatus = Track.CopyStatus.NONE, originalAvailable = true)
                )
                val rowId = database.trackDao().insertIgnore(entity)
                if (rowId != -1L) newCount++
            } else {
                val merged = existing.toDomain().copy(
                    title = candidate.title, artist = candidate.artist, album = candidate.album,
                    durationMs = candidate.durationMs, artworkUri = candidate.artworkUri,
                    dateAdded = candidate.dateAdded, uri = candidate.uri, originalAvailable = true
                )
                val wasPermanent = existing.nextAttemptAt >= Long.MAX_VALUE
                val uriChanged = existing.uri != candidate.uri
                val wasUnavailable = !existing.originalAvailable
                val shouldResetRetry = wasPermanent && (uriChanged || wasUnavailable)

                val mergedEntity = TrackEntity.fromDomain(merged).copy(
                    nextAttemptAt = if (shouldResetRetry) 0L else existing.nextAttemptAt
                )
                database.trackDao().upsert(mergedEntity)
            }
        }

        BackupScheduler.scheduleCopy(context)

        ScanResult(
            newCount = newCount,
            scannedStableKeys = outcome.tracks.map { it.stableKey }.toSet(),
            isComplete = true,
            errorMessage = null
        )
    }

    suspend fun syncLibraryStatus(scannedStableKeys: Set<String>) = withContext(Dispatchers.IO) {
        val all = database.trackDao().getAll()
        if (all.isEmpty()) return@withContext
        if (scannedStableKeys.isEmpty()) return@withContext

        for (entity in all) {
            if (entity.stableKey !in scannedStableKeys && entity.originalAvailable) {
                database.trackDao().setOriginalAvailable(entity.id, false)
            }
        }
    }

    suspend fun persistEvent(event: PlayEvent) = withContext(Dispatchers.IO) {
        database.playEventDao().insert(PlayEventEntity.fromDomain(event))
    }
}