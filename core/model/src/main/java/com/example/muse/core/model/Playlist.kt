package com.example.muse.core.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Playlist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class PlaylistTrack(
    val playlistId: String,
    val trackId: String,
    val position: Int
)