package com.example.muse.core.media

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.muse.core.model.Track
import java.io.File
import java.util.UUID

object MediaItemFactory {

    data class CreateAllResult(
        val mediaItems: List<MediaItem>,
        val sourceIndices: List<Int>
    )

    fun create(track: Track): MediaItem {
        val playbackUri = resolvePlaybackUri(track)
            ?: throw IllegalStateException("Track ${track.id} has no available source")
        return createWithUri(track, playbackUri)
    }

    fun createAll(tracks: List<Track>): CreateAllResult {
        val items = mutableListOf<MediaItem>()
        val indices = mutableListOf<Int>()
        tracks.forEachIndexed { index, track ->
            val uri = resolvePlaybackUri(track)
            if (uri != null) {
                items.add(createWithUri(track, uri))
                indices.add(index)
            }
        }
        return CreateAllResult(items, indices)
    }

    private fun resolvePlaybackUri(track: Track): String? {
        if (track.originalAvailable && track.uri.isNotBlank()) {
            return track.uri
        }
        val copyAvailable = track.copyStatus == Track.CopyStatus.COMPLETED ||
                track.copyStatus == Track.CopyStatus.COMPLETED_UNVERIFIED
        if (copyAvailable && track.museCopyPath != null) {
            val f = File(track.museCopyPath)
            if (f.exists() && f.length() > 0L) {
                return f.toURI().toString()
            }
        }
        return null
    }

    private fun createWithUri(track: Track, playbackUri: String): MediaItem {
        val playbackItemId = UUID.randomUUID().toString()
        return MediaItem.Builder()
            .setMediaId(playbackItemId)
            .setUri(playbackUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .setAlbumTitle(track.album)
                    .setArtworkUri(track.artworkUri?.let { android.net.Uri.parse(it) })
                    .setExtras(Bundle().apply { putString("trackId", track.id) })
                    .build()
            )
            .build()
    }
}