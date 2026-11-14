package com.example.muse.core.media

import com.example.muse.core.model.EventType

/**
 * 纯逻辑决策器，与 Android 框架解耦，便于单元测试。
 * PlaybackService 将 Player 事件的状态快照传入，由本类决定是否发出事件。
 */
class PlaybackEventDecider {

    data class Decision(
        val eventType: EventType?,
        val shouldUpdateStartEmitted: Boolean = false,
        val shouldResetUserPaused: Boolean = false
    )

    /**
     * 曲目切换时的决策。
     * @param oldTrackId 旧曲目 ID（null 表示首次）
     * @param oldAttemptId 旧尝试 ID
     * @param oldPosition 旧曲目位置
     * @param oldCompletedNaturally 旧曲目是否自然完成
     * @param transitionReason MediaItemTransition reason
     * @param newTrackId 新曲目 ID
     */
    fun onMediaItemTransition(
        oldTrackId: String?,
        oldAttemptId: String?,
        oldPosition: Long,
        oldCompletedNaturally: Boolean,
        transitionReason: Int,
        newTrackId: String?
    ): TransitionDecision {
        val skipDecision = if (oldTrackId != null &&
            !oldCompletedNaturally &&
            transitionReason == 2 && // MEDIA_ITEM_TRANSITION_REASON_SEEK
            oldTrackId != newTrackId
        ) {
            SkipEvent(oldTrackId, oldAttemptId ?: "", oldPosition)
        } else null

        return TransitionDecision(skipEvent = skipDecision)
    }

    data class TransitionDecision(val skipEvent: SkipEvent?)
    data class SkipEvent(val trackId: String, val playbackAttemptId: String, val positionMs: Long)

    /**
     * 暂停/恢复决策。
     * @param playWhenReady 新的 playWhenReady 值
     * @param reason PlayWhenReadyChangeReason
     * @param userPaused 当前 userPaused 状态
     * @param startEmitted 当前曲目是否已发出 PLAY_START
     */
    fun onPlayWhenReadyChanged(
        playWhenReady: Boolean,
        reason: Int,
        userPaused: Boolean,
        startEmitted: Boolean
    ): PlayWhenReadyDecision {
        // USER_REQUEST = 1 (Media3 常量)
        val isUserRequest = reason == 1

        return if (!playWhenReady && isUserRequest && startEmitted) {
            PlayWhenReadyDecision(
                eventType = EventType.PLAY_PAUSE,
                newUserPaused = true,
                newStartEmitted = startEmitted
            )
        } else if (playWhenReady && userPaused && startEmitted) {
            PlayWhenReadyDecision(
                eventType = EventType.PLAY_RESUME,
                newUserPaused = false,
                newStartEmitted = startEmitted
            )
        } else if (playWhenReady && !startEmitted) {
            // 首次就绪开始播放（如果 isPlaying 尚未触发）
            PlayWhenReadyDecision(
                eventType = EventType.PLAY_START,
                newUserPaused = false,
                newStartEmitted = true
            )
        } else {
            PlayWhenReadyDecision(
                eventType = null,
                newUserPaused = userPaused,
                newStartEmitted = startEmitted
            )
        }
    }

    data class PlayWhenReadyDecision(
        val eventType: EventType?,
        val newUserPaused: Boolean,
        val newStartEmitted: Boolean
    )

    /**
     * 播放状态变化决策。
     */
    fun onPlaybackStateChanged(
        playbackState: Int,
        playWhenReady: Boolean,
        isPlaying: Boolean,
        startEmitted: Boolean,
        trackCompletedNaturally: Boolean
    ): StateDecision {
        return when (playbackState) {
            // STATE_READY = 3
            3 -> {
                if (!startEmitted && playWhenReady && isPlaying) {
                    StateDecision(eventType = EventType.PLAY_START, newStartEmitted = true)
                } else {
                    StateDecision(eventType = null, newStartEmitted = startEmitted)
                }
            }
            // STATE_ENDED = 4
            4 -> {
                if (!trackCompletedNaturally) {
                    StateDecision(
                        eventType = EventType.PLAY_COMPLETE,
                        newStartEmitted = startEmitted,
                        newTrackCompletedNaturally = true
                    )
                } else {
                    StateDecision(eventType = null, newStartEmitted = startEmitted)
                }
            }
            else -> StateDecision(eventType = null, newStartEmitted = startEmitted)
        }
    }

    data class StateDecision(
        val eventType: EventType?,
        val newStartEmitted: Boolean,
        val newTrackCompletedNaturally: Boolean = false
    )

    /**
     * 位置不连续决策（SEEK）。
     */
    fun onPositionDiscontinuity(
        reason: Int,
        oldMediaItemIndex: Int,
        newMediaItemIndex: Int,
        startEmitted: Boolean
    ): SeekDecision {
        // DISCONTINUITY_REASON_SEEK = 1
        val isSeek = reason == 1
        val sameItem = oldMediaItemIndex == newMediaItemIndex
        return if (isSeek && sameItem && startEmitted) {
            SeekDecision(shouldEmitSeek = true)
        } else {
            SeekDecision(shouldEmitSeek = false)
        }
    }

    data class SeekDecision(val shouldEmitSeek: Boolean)
}