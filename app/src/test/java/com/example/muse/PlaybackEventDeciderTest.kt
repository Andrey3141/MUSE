package com.example.muse

import com.example.muse.core.media.PlaybackEventDecider
import com.example.muse.core.model.EventType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PlaybackEventDeciderTest {

    private lateinit var decider: PlaybackEventDecider

    @Before
    fun setup() {
        decider = PlaybackEventDecider()
    }

    @Test
    fun `first playback emits PLAY_START`() {
        val decision = decider.onPlaybackStateChanged(
            playbackState = 3,
            playWhenReady = true,
            isPlaying = true,
            startEmitted = false,
            trackCompletedNaturally = false
        )
        assertEquals(EventType.PLAY_START, decision.eventType)
        assertTrue(decision.newStartEmitted)
    }

    @Test
    fun `new track after transition emits PLAY_START`() {
        val transition = decider.onMediaItemTransition(
            oldTrackId = "track-a",
            oldAttemptId = "attempt-a",
            oldPosition = 1000L,
            oldCompletedNaturally = true,
            transitionReason = 1,
            newTrackId = "track-b"
        )
        assertNull(transition.skipEvent)

        val state = decider.onPlaybackStateChanged(
            playbackState = 3,
            playWhenReady = true,
            isPlaying = true,
            startEmitted = false,
            trackCompletedNaturally = false
        )
        assertEquals(EventType.PLAY_START, state.eventType)
    }

    @Test
    fun `returning to previous track emits PLAY_START again`() {
        val transition = decider.onMediaItemTransition(
            oldTrackId = "track-b",
            oldAttemptId = "attempt-b",
            oldPosition = 2000L,
            oldCompletedNaturally = true,
            transitionReason = 1,
            newTrackId = "track-a"
        )
        assertNull(transition.skipEvent)

        val state = decider.onPlaybackStateChanged(
            playbackState = 3,
            playWhenReady = true,
            isPlaying = true,
            startEmitted = false,
            trackCompletedNaturally = false
        )
        assertEquals(EventType.PLAY_START, state.eventType)
    }

    @Test
    fun `natural completion emits exactly one PLAY_COMPLETE`() {
        val first = decider.onPlaybackStateChanged(
            playbackState = 4,
            playWhenReady = true,
            isPlaying = false,
            startEmitted = true,
            trackCompletedNaturally = false
        )
        assertEquals(EventType.PLAY_COMPLETE, first.eventType)
        assertTrue(first.newTrackCompletedNaturally)

        val second = decider.onPlaybackStateChanged(
            playbackState = 4,
            playWhenReady = true,
            isPlaying = false,
            startEmitted = true,
            trackCompletedNaturally = true
        )
        assertNull(second.eventType)
    }

    @Test
    fun `user skip emits SKIP_EARLY with old position`() {
        val decision = decider.onMediaItemTransition(
            oldTrackId = "track-a",
            oldAttemptId = "attempt-a1",
            oldPosition = 5000L,
            oldCompletedNaturally = false,
            transitionReason = 2,
            newTrackId = "track-b"
        )
        // 使用 requireNotNull 取代 assertNotNull + !!：
        // - 失败时抛 IllegalArgumentException，信息更明确
        // - 返回非空类型，后续访问无需 !!
        val skip = requireNotNull(decision.skipEvent) {
            "Expected SKIP_EARLY event"
        }
        assertEquals("track-a", skip.trackId)
        assertEquals("attempt-a1", skip.playbackAttemptId)
        assertEquals(5000L, skip.positionMs)
    }

    @Test
    fun `auto transition does not emit SKIP_EARLY`() {
        val decision = decider.onMediaItemTransition(
            oldTrackId = "track-a",
            oldAttemptId = "attempt-a1",
            oldPosition = 180000L,
            oldCompletedNaturally = true,
            transitionReason = 1,
            newTrackId = "track-b"
        )
        assertNull(decision.skipEvent)
    }

    @Test
    fun `buffering does not emit PAUSE or RESUME`() {
        val decision = decider.onPlayWhenReadyChanged(
            playWhenReady = true,
            reason = 1,
            userPaused = false,
            startEmitted = true
        )
        assertNull(decision.eventType)
    }

    @Test
    fun `user pause emits PLAY_PAUSE`() {
        val decision = decider.onPlayWhenReadyChanged(
            playWhenReady = false,
            reason = 1,
            userPaused = false,
            startEmitted = true
        )
        assertEquals(EventType.PLAY_PAUSE, decision.eventType)
        assertTrue(decision.newUserPaused)
    }

    @Test
    fun `user resume after pause emits PLAY_RESUME`() {
        val decision = decider.onPlayWhenReadyChanged(
            playWhenReady = true,
            reason = 1,
            userPaused = true,
            startEmitted = true
        )
        assertEquals(EventType.PLAY_RESUME, decision.eventType)
        assertFalse(decision.newUserPaused)
    }

    @Test
    fun `seek within same track emits SEEK not SKIP_EARLY`() {
        val decision = decider.onPositionDiscontinuity(
            reason = 1,
            oldMediaItemIndex = 0,
            newMediaItemIndex = 0,
            startEmitted = true
        )
        assertTrue(decision.shouldEmitSeek)

        val crossItem = decider.onPositionDiscontinuity(
            reason = 1,
            oldMediaItemIndex = 0,
            newMediaItemIndex = 1,
            startEmitted = true
        )
        assertFalse(crossItem.shouldEmitSeek)
    }
}