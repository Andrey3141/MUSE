package com.example.muse.core.media

enum class TransitionReason {
    REPEAT,
    AUTO,
    USER_SEEK,
    PLAYLIST_CHANGED,
    UNKNOWN
}

enum class PlaybackState {
    IDLE,
    BUFFERING,
    READY,
    ENDED
}