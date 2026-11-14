package com.example.muse.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.muse.core.model.Track

@Entity(
    tableName = "tracks",
    indices = [Index(value = ["stableKey"], unique = true)]
)
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val uri: String,
    val artworkUri: String?,
    val dateAdded: Long,
    val stableKey: String,
    val vectorVersion: Int,
    val analyzedAt: Long?,
    val museCopyPath: String?,
    val copyStatus: String,
    val originalAvailable: Boolean,
    val nextAttemptAt: Long = 0L,
    /**
     * Fencing token. Монотонно возрастает при каждом захвате.
     * 0 = никем не захвачен.
     */
    val claimEpoch: Long = 0L,
    /** Время последнего захвата (мс). Используется для определения устаревшего claim. */
    val claimedAt: Long = 0L
) {
    fun toDomain() = Track(
        id = id, title = title, artist = artist, album = album,
        durationMs = durationMs, uri = uri, artworkUri = artworkUri,
        dateAdded = dateAdded, stableKey = stableKey,
        vectorVersion = vectorVersion, analyzedAt = analyzedAt,
        museCopyPath = museCopyPath,
        copyStatus = copyStatus,
        originalAvailable = originalAvailable
    )

    companion object {
        fun fromDomain(t: Track) = TrackEntity(
            id = t.id, title = t.title, artist = t.artist, album = t.album,
            durationMs = t.durationMs, uri = t.uri, artworkUri = t.artworkUri,
            dateAdded = t.dateAdded, stableKey = t.stableKey,
            vectorVersion = t.vectorVersion, analyzedAt = t.analyzedAt,
            museCopyPath = t.museCopyPath,
            copyStatus = t.copyStatus,
            originalAvailable = t.originalAvailable
        )
    }
}