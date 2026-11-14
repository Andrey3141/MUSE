package com.example.muse.core.media

import com.example.muse.core.model.PlayEvent

/**
 * 由 app 模块实现，用于将 PlaybackService 产生的事件持久化。
 * 定义在 core:media 中，避免服务直接依赖 app。
 */
fun interface PlaybackEventListener {
    fun onPlayEvent(event: PlayEvent)
}