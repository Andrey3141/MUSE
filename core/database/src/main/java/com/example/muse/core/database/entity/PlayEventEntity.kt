package com.example.muse.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.muse.core.model.EventType
import com.example.muse.core.model.PlayEvent

@Entity(tableName = "play_events")
data class PlayEventEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val playbackAttemptId: String,
    val trackId: String,
    val playbackItemId: String,
    val eventType: String,
    val positionMs: Long,
    val timestamp: Long,
    val synced: Boolean
) {
    fun toDomain() = PlayEvent(
        id = id,
        sessionId = sessionId,
        playbackAttemptId = playbackAttemptId,
        trackId = trackId,
        playbackItemId = playbackItemId,
        eventType = EventType.valueOf(eventType),
        positionMs = positionMs,
        timestamp = timestamp,
        synced = synced
    )

    companion object {
        fun fromDomain(e: PlayEvent) = PlayEventEntity(
            id = e.id,
            sessionId = e.sessionId,
            playbackAttemptId = e.playbackAttemptId,
            trackId = e.trackId,
            playbackItemId = e.playbackItemId,
            eventType = e.eventType.name,
            positionMs = e.positionMs,
            timestamp = e.timestamp,
            synced = e.synced
        )
    }
}