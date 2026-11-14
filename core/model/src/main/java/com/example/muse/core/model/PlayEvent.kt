package com.example.muse.core.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PlayEvent(
    val id: String = UUID.randomUUID().toString(),
    /** 用户会话 ID，跨曲目保持 */
    val sessionId: String,
    /** 单次曲目播放尝试 ID */
    val playbackAttemptId: String,
    /** 音乐曲目 ID（稳定，跨队列元素相同） */
    val trackId: String,
    /** 队列元素 ID（同一曲目在队列中多次出现时不同） */
    val playbackItemId: String,
    val eventType: EventType,
    val positionMs: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)

@Serializable
enum class EventType {
    PLAY_START,
    PLAY_PAUSE,
    PLAY_RESUME,
    PLAY_COMPLETE,
    SKIP_EARLY,
    SEEK,
    VOLUME_CHANGE,
    REPEAT,
    LIKE,
    DISLIKE,
    ADD_TO_PLAYLIST
}