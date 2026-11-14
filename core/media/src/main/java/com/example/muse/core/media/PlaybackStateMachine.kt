package com.example.muse.core.media

import com.example.muse.core.model.EventType
import com.example.muse.core.model.PlayEvent
import java.util.UUID

/**
 * 简化后的状态机。
 *
 * 本轮关键修正：
 * - sameKnownItem 谓词：仅当两个 ID 均非 null 且相等时视为同一 item
 * - REPEAT 后重复 ENDED 不生成第二个 PLAY_COMPLETE（currentStartEmitted == false 时拒绝）
 * - userPaused 在 attempt 切换时重置，防止跨 attempt 泄漏
 * - PLAY_COMPLETE 仅由 onPlaybackStateChanged(ENDED) 生成
 */
class PlaybackStateMachine(
    private val idProvider: () -> String = { UUID.randomUUID().toString() }
) {

    private var sessionId: String? = null
    private var sessionActive: Boolean = false

    private var currentTrackId: String? = null
    private var currentPlaybackItemId: String? = null
    private var currentAttemptId: String? = null
    private var currentStartEmitted: Boolean = false

    /** 当前 attempt 是否已有终态事件（PLAY_COMPLETE）。 */
    private var currentAttemptTerminal: Boolean = false

    /** 用户暂停状态。仅在当前 attempt 的 PLAY_START 之后有效。 */
    private var userPaused: Boolean = false

    // ========== 谓词 ==========

    /**
     * 仅当两个 ID 均非 null 且相等时，视为同一已知 item。
     * null 不被视为"匹配"。
     */
    private fun sameKnownItem(a: String?, b: String?): Boolean {
        return a != null && b != null && a == b
    }

    // ========== 过渡处理 ==========

    fun onMediaItemTransition(
        newTrackId: String?,
        newPlaybackItemId: String?,
        transitionReason: TransitionReason
    ) {
        // PLAYLIST_CHANGED 同一已知 item：不改变状态
        if (transitionReason == TransitionReason.PLAYLIST_CHANGED &&
            sameKnownItem(currentPlaybackItemId, newPlaybackItemId)
        ) {
            return
        }

        // REPEAT 同一已知 item：重置 startEmitted，允许新 attempt
        // 不生成 PLAY_COMPLETE（无法证明自然完成）
        if (transitionReason == TransitionReason.REPEAT &&
            sameKnownItem(currentPlaybackItemId, newPlaybackItemId)
        ) {
            currentStartEmitted = false
            currentAttemptTerminal = false
            userPaused = false  // 新 attempt 不继承旧暂停状态
            return
        }

        // 其他情况：item 确实变了（或 null ID 导致无法匹配）
        // 关闭旧 attempt 的暂停状态，重置曲目
        currentTrackId = newTrackId
        currentPlaybackItemId = newPlaybackItemId
        currentAttemptId = null
        currentStartEmitted = false
        currentAttemptTerminal = false
        userPaused = false  // 新曲目不继承旧暂停状态
    }

    // ========== 实际开始播放 ==========

    fun tryStartCurrentTrack(): PlayEvent? {
        val trackId = currentTrackId ?: return null
        val itemId = currentPlaybackItemId ?: return null
        if (currentStartEmitted) return null
        if (currentAttemptTerminal) return null

        if (!sessionActive) {
            sessionId = idProvider()
            sessionActive = true
        }

        val attemptId = idProvider()
        currentAttemptId = attemptId
        currentStartEmitted = true
        currentAttemptTerminal = false
        userPaused = false  // 新 attempt 从非暂停状态开始

        return PlayEvent(
            sessionId = sessionId!!,
            playbackAttemptId = attemptId,
            trackId = trackId,
            playbackItemId = itemId,
            eventType = EventType.PLAY_START,
            positionMs = 0L
        )
    }

    // ========== 暂停/恢复 ==========

    fun onUserPause(positionMs: Long): PlayEvent? {
        if (userPaused) return null
        val trackId = currentTrackId ?: return null
        val itemId = currentPlaybackItemId ?: return null
        val attemptId = currentAttemptId ?: return null
        val sid = sessionId ?: return null
        if (!currentStartEmitted) return null
        if (currentAttemptTerminal) return null

        userPaused = true
        return PlayEvent(
            sessionId = sid,
            playbackAttemptId = attemptId,
            trackId = trackId,
            playbackItemId = itemId,
            eventType = EventType.PLAY_PAUSE,
            positionMs = positionMs
        )
    }

    fun onUserResume(positionMs: Long): PlayEvent? {
        if (!userPaused) return null
        val trackId = currentTrackId ?: return null
        val itemId = currentPlaybackItemId ?: return null
        val attemptId = currentAttemptId ?: return null
        val sid = sessionId ?: return null
        if (!currentStartEmitted) return null
        if (currentAttemptTerminal) return null

        userPaused = false
        return PlayEvent(
            sessionId = sid,
            playbackAttemptId = attemptId,
            trackId = trackId,
            playbackItemId = itemId,
            eventType = EventType.PLAY_RESUME,
            positionMs = positionMs
        )
    }

    fun onSystemPlayWhenReadyChange() {}

    // ========== 播放状态（唯一 PLAY_COMPLETE 来源） ==========

    fun onPlaybackStateChanged(
        state: PlaybackState,
        positionMs: Long
    ): PlayEvent? {
        // 关键修正：如果当前没有已开始的 attempt，ENDED 不生成事件
        // 这防止 REPEAT 后、新 PLAY_START 前的重复 ENDED 为旧 attempt 生成第二个 COMPLETE
        if (!currentStartEmitted) return null

        val trackId = currentTrackId ?: return null
        val itemId = currentPlaybackItemId ?: return null
        val attemptId = currentAttemptId ?: return null
        val sid = sessionId ?: return null

        if (state == PlaybackState.ENDED) {
            if (currentAttemptTerminal) return null

            currentAttemptTerminal = true

            return PlayEvent(
                sessionId = sid,
                playbackAttemptId = attemptId,
                trackId = trackId,
                playbackItemId = itemId,
                eventType = EventType.PLAY_COMPLETE,
                positionMs = positionMs
            )
        }
        return null
    }

    // ========== 曲目内 Seek ==========

    fun onSeekWithinTrack(newPositionMs: Long): PlayEvent? {
        val trackId = currentTrackId ?: return null
        val itemId = currentPlaybackItemId ?: return null
        val attemptId = currentAttemptId ?: return null
        val sid = sessionId ?: return null
        if (!currentStartEmitted) return null
        if (currentAttemptTerminal) return null

        return PlayEvent(
            sessionId = sid,
            playbackAttemptId = attemptId,
            trackId = trackId,
            playbackItemId = itemId,
            eventType = EventType.SEEK,
            positionMs = newPositionMs
        )
    }

    // ========== 显式 Stop ==========

    fun onStop() {
        sessionActive = false
        sessionId = null
        currentTrackId = null
        currentPlaybackItemId = null
        currentAttemptId = null
        currentStartEmitted = false
        currentAttemptTerminal = false
        userPaused = false
    }

    fun resyncWithPlayerTrack(trackId: String?, playbackItemId: String?) {
        if (currentTrackId == null && trackId != null) {
            currentTrackId = trackId
            currentPlaybackItemId = playbackItemId
        }
    }

    // ========== 供测试观察 ==========
    fun currentSessionId(): String? = sessionId
    fun currentAttemptId(): String? = currentAttemptId
    fun isSessionActive(): Boolean = sessionActive
    fun isUserPaused(): Boolean = userPaused
}