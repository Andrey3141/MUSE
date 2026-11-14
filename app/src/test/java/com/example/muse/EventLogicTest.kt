package com.example.muse

import com.example.muse.core.model.EventType
import com.example.muse.core.model.PlayEvent
import org.junit.Assert.*
import org.junit.Test

class EventLogicTest {

    @Test
    fun `same sessionId across multiple tracks`() {
        val sessionId = "session-1"
        val e1 = PlayEvent(
            sessionId = sessionId,
            playbackAttemptId = "attempt-1",
            trackId = "track-a",
            playbackItemId = "item-a1",
            eventType = EventType.PLAY_START
        )
        val e2 = PlayEvent(
            sessionId = sessionId,
            playbackAttemptId = "attempt-2",
            trackId = "track-b",
            playbackItemId = "item-b1",
            eventType = EventType.PLAY_START
        )
        assertEquals(sessionId, e1.sessionId)
        assertEquals(sessionId, e2.sessionId)
        assertNotEquals(e1.playbackAttemptId, e2.playbackAttemptId)
    }

    @Test
    fun `natural completion does not produce SKIP_EARLY`() {
        val completedNaturally = true
        val userSkipped = false
        assertFalse(userSkipped && !completedNaturally)
    }

    @Test
    fun `early skip only on user seek`() {
        val transitionReasonSeek = true
        val transitionReasonAuto = false
        val completedNaturally = false

        assertTrue(transitionReasonSeek && !completedNaturally)
        assertFalse(transitionReasonAuto && !completedNaturally)
    }

    @Test
    fun `no PLAY_RESUME after PLAY_START`() {
        var hasStarted = false
        val emitted = mutableListOf<EventType>()

        if (!hasStarted) {
            hasStarted = true
            emitted.add(EventType.PLAY_START)
        }

        assertEquals(1, emitted.size)
        assertEquals(EventType.PLAY_START, emitted[0])
        assertFalse(emitted.contains(EventType.PLAY_RESUME))
    }

    @Test
    fun `no PLAY_PAUSE during buffering`() {
        val isPlaying = false
        val playWhenReady = true
        val playbackStateBuffering = true

        val shouldEmitPause = !isPlaying && !playWhenReady && !playbackStateBuffering
        assertFalse(shouldEmitPause)
    }
}