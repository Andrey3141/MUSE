package com.example.muse

import com.example.muse.core.model.EventType
import com.example.muse.core.model.PlayEvent
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class SessionGroupingTest {

    @Test
    fun `events from same session share sessionId`() {
        val sessionId = UUID.randomUUID().toString()
        val trackId = "track-1"
        val itemId = "item-1"
        val attemptId = "attempt-1"
        val events = listOf(
            PlayEvent(
                sessionId = sessionId,
                playbackAttemptId = attemptId,
                trackId = trackId,
                playbackItemId = itemId,
                eventType = EventType.PLAY_START
            ),
            PlayEvent(
                sessionId = sessionId,
                playbackAttemptId = attemptId,
                trackId = trackId,
                playbackItemId = itemId,
                eventType = EventType.PLAY_PAUSE
            ),
            PlayEvent(
                sessionId = sessionId,
                playbackAttemptId = attemptId,
                trackId = trackId,
                playbackItemId = itemId,
                eventType = EventType.PLAY_RESUME
            ),
            PlayEvent(
                sessionId = sessionId,
                playbackAttemptId = attemptId,
                trackId = trackId,
                playbackItemId = itemId,
                eventType = EventType.PLAY_COMPLETE
            )
        )
        val grouped = events.groupBy { it.sessionId }
        assertEquals(1, grouped.size)
        assertEquals(4, grouped[sessionId]?.size)
    }

    @Test
    fun `different sessions have different ids`() {
        val s1 = UUID.randomUUID().toString()
        val s2 = UUID.randomUUID().toString()
        val e1 = PlayEvent(
            sessionId = s1,
            playbackAttemptId = "attempt-1",
            trackId = "t1",
            playbackItemId = "item-1",
            eventType = EventType.PLAY_START
        )
        val e2 = PlayEvent(
            sessionId = s2,
            playbackAttemptId = "attempt-2",
            trackId = "t1",
            playbackItemId = "item-1",
            eventType = EventType.PLAY_START
        )
        assertEquals(2, listOf(e1, e2).groupBy { it.sessionId }.size)
    }
}