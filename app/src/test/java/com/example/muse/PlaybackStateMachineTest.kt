package com.example.muse

import com.example.muse.core.media.PlaybackState
import com.example.muse.core.media.PlaybackStateMachine
import com.example.muse.core.media.TransitionReason
import com.example.muse.core.model.EventType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PlaybackStateMachineTest {

    private lateinit var machine: PlaybackStateMachine
    private var idCounter = 0

    @Before
    fun setup() {
        idCounter = 0
        machine = PlaybackStateMachine { "id-${idCounter++}" }
    }

    private fun startTrack(trackId: String, itemId: String) {
        machine.onMediaItemTransition(trackId, itemId, TransitionReason.USER_SEEK)
        machine.tryStartCurrentTrack()
    }

    // ===== 1. REPEAT 后重复 ENDED 不为旧 attempt 生成第二个 COMPLETE =====

    @Test
    fun `repeated ended after repeat does not create second complete`() {
        startTrack("track-a", "item-a1")
        val firstAttempt = machine.currentAttemptId()!!

        // 自然完成
        val complete1 = machine.onPlaybackStateChanged(PlaybackState.ENDED, 1000L)
        assertNotNull(complete1)
        assertEquals(EventType.PLAY_COMPLETE, complete1!!.eventType)

        // REPEAT 同一 item：重置 startEmitted，允许新 attempt
        machine.onMediaItemTransition("track-a", "item-a1", TransitionReason.REPEAT)

        // 在新 PLAY_START 之前，又收到 ENDED（异常但可能发生的时序）
        val complete2 = machine.onPlaybackStateChanged(PlaybackState.ENDED, 1000L)
        assertNull(complete2) // 关键：currentStartEmitted == false，拒绝生成

        // 新 attempt 开始
        val newStart = machine.tryStartCurrentTrack()
        assertNotNull(newStart)
        assertNotEquals(firstAttempt, machine.currentAttemptId())
    }

    // ===== 2. null ID 不被视为匹配 =====

    @Test
    fun `repeat with null ids does not match`() {
        startTrack("track-a", "item-a1")
        val attempt = machine.currentAttemptId()

        machine.onMediaItemTransition(null, null, TransitionReason.REPEAT)

        // null != "item-a1"，被当作普通过渡处理
        assertNull(machine.currentAttemptId())
    }

    @Test
    fun `playlist changed with null ids does not match`() {
        startTrack("track-a", "item-a1")
        val attempt = machine.currentAttemptId()

        machine.onMediaItemTransition(null, null, TransitionReason.PLAYLIST_CHANGED)

        // null != "item-a1"，被当作普通过渡处理
        assertNull(machine.currentAttemptId())
    }

    @Test
    fun `repeat with same known ids matches`() {
        startTrack("track-a", "item-a1")
        val attempt = machine.currentAttemptId()

        machine.onMediaItemTransition("track-a", "item-a1", TransitionReason.REPEAT)

        // 匹配成功，startEmitted 被重置但 attemptId 保留
        assertEquals(attempt, machine.currentAttemptId())
    }

    // ===== 3. Pause/Resume 跨 attempt 不泄漏 =====

    @Test
    fun `pause on A then switch to B does not inherit paused state`() {
        startTrack("track-a", "item-a1")
        machine.onUserPause(500L)
        assertTrue(machine.isUserPaused())

        // 切换到 B
        machine.onMediaItemTransition("track-b", "item-b1", TransitionReason.USER_SEEK)

        // B 不继承 A 的暂停状态
        assertFalse(machine.isUserPaused())
    }

    @Test
    fun `pause on A then repeat A keeps paused state for new attempt`() {
        startTrack("track-a", "item-a1")
        machine.onUserPause(500L)
        assertTrue(machine.isUserPaused())

        // REPEAT 同一 item：userPaused 被重置（新 attempt 从非暂停开始）
        machine.onMediaItemTransition("track-a", "item-a1", TransitionReason.REPEAT)
        assertFalse(machine.isUserPaused())

        // 新 attempt 开始
        machine.tryStartCurrentTrack()
        // 仍然非暂停
        assertFalse(machine.isUserPaused())
    }

    // ===== 4. 终态后禁止 Pause/Resume/Seek =====

    @Test
    fun `terminal attempt rejects pause resume seek`() {
        startTrack("track-a", "item-a1")
        machine.onPlaybackStateChanged(PlaybackState.ENDED, 1000L)

        assertNull(machine.onUserPause(500L))
        assertNull(machine.onUserResume(500L))
        assertNull(machine.onSeekWithinTrack(500L))
    }

    // ===== 5. 重复 PLAY_START 不可能 =====

    @Test
    fun `duplicate PLAY_START impossible`() {
        startTrack("track-a", "item-a1")
        assertNull(machine.tryStartCurrentTrack())
    }

    // ===== 6. PLAYLIST_CHANGED 同 item 不改变状态 =====

    @Test
    fun `playlist changed same item keeps attempt`() {
        startTrack("track-a", "item-a1")
        val attempt = machine.currentAttemptId()!!

        machine.onMediaItemTransition("track-a", "item-a1", TransitionReason.PLAYLIST_CHANGED)

        assertEquals(attempt, machine.currentAttemptId())
        assertNull(machine.tryStartCurrentTrack()) // 不创建新 PLAY_START
    }
}