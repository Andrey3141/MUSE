package com.example.muse.core.media

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.example.muse.core.model.Track

data class ScanOutcome(
    val tracks: List<Track>,
    val isComplete: Boolean,
    val errorMessage: String? = null
)

object MediaStoreScanner {

    fun scanAudio(context: Context): ScanOutcome {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val tracks = mutableListOf<Track>()

        val cursor: Cursor? = try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection, null,
                "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            )
        } catch (e: SecurityException) {
            return ScanOutcome(emptyList(), isComplete = false, errorMessage = "Нет разрешения на чтение MediaStore")
        } catch (e: Exception) {
            return ScanOutcome(emptyList(), isComplete = false, errorMessage = "Ошибка запроса MediaStore: ${e.message}")
        }

        if (cursor == null) {
            return ScanOutcome(emptyList(), isComplete = false, errorMessage = "MediaStore вернул null cursor")
        }

        return try {
            val volume = "external"
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val addCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val albCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val mediaId = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaId
                )
                val albumId = cursor.getLong(albCol)
                val artworkUri = ContentUris.withAppendedId(
                    android.net.Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                val fileSize = if (sizeCol >= 0 && !cursor.isNull(sizeCol)) {
                    cursor.getLong(sizeCol)
                } else null

                tracks.add(
                    Track(
                        title = cursor.getString(titleCol) ?: "Unknown",
                        artist = cursor.getString(artistCol) ?: "Unknown",
                        album = cursor.getString(albumCol) ?: "Unknown",
                        durationMs = cursor.getLong(durCol),
                        uri = uri.toString(),
                        artworkUri = artworkUri.toString(),
                        dateAdded = cursor.getLong(addCol) * 1000,
                        stableKey = "mediastore:$volume:$mediaId"
                    )
                )
            }
            ScanOutcome(tracks, isComplete = true, errorMessage = null)
        } catch (e: Exception) {
            ScanOutcome(emptyList(), isComplete = false, errorMessage = "Ошибка чтения cursor: ${e.message}")
        } finally {
            cursor.close()
        }
    }
}