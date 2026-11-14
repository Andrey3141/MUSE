package com.example.muse.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.muse.core.model.Playlist
import com.example.muse.core.model.PlaylistTrack

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long
) {
    fun toDomain() = Playlist(id, name, createdAt)
    companion object {
        fun fromDomain(p: Playlist) = PlaylistEntity(p.id, p.name, p.createdAt)
    }
}

@Entity(tableName = "playlist_tracks", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackEntity(
    val playlistId: String,
    val trackId: String,
    val position: Int
) {
    fun toDomain() = PlaylistTrack(playlistId, trackId, position)
    companion object {
        fun fromDomain(pt: PlaylistTrack) =
            PlaylistTrackEntity(pt.playlistId, pt.trackId, pt.position)
    }
}