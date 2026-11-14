package com.example.muse.core.media

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingSimpleBasePlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var exoPlayer: ExoPlayer? = null

    private val stateMachine = PlaybackStateMachine()

    private var sessionActivity: PendingIntent? = null

    private val eventListener: PlaybackEventListener?
        get() = applicationContext as? PlaybackEventListener

    fun setSessionActivity(pendingIntent: PendingIntent) {
        sessionActivity = pendingIntent
        mediaSession?.setSessionActivity(pendingIntent)
    }

    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        exoPlayer = player

        val forwarding = object : ForwardingSimpleBasePlayer(player) {
            // 不重写任何 handle 方法
            // 暂停/恢复在 onPlayWhenReadyChanged 中处理
            // 过渡在 onMediaItemTransition 中处理
        }

        player.addListener(object : Player.Listener {

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val newTrackId = extractTrackId(mediaItem)
                val newItemId = extractPlaybackItemId(mediaItem)
                val transitionReason = when (reason) {
                    Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> TransitionReason.REPEAT
                    Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> TransitionReason.AUTO
                    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> TransitionReason.USER_SEEK
                    Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> TransitionReason.PLAYLIST_CHANGED
                    else -> TransitionReason.UNKNOWN
                }

                stateMachine.onMediaItemTransition(newTrackId, newItemId, transitionReason)

                if (player.isPlaying && newItemId != null) {
                    stateMachine.tryStartCurrentTrack()?.let { emitEvent(it) }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val state = when (playbackState) {
                    Player.STATE_IDLE -> PlaybackState.IDLE
                    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                    Player.STATE_READY -> PlaybackState.READY
                    Player.STATE_ENDED -> PlaybackState.ENDED
                    else -> PlaybackState.IDLE
                }

                stateMachine.onPlaybackStateChanged(state, player.currentPosition)
                    ?.let { emitEvent(it) }

                if (state == PlaybackState.READY && player.playWhenReady && player.isPlaying) {
                    stateMachine.tryStartCurrentTrack()?.let { emitEvent(it) }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST) {
                    if (playWhenReady) {
                        stateMachine.onUserResume(player.currentPosition)?.let { emitEvent(it) }
                    } else {
                        stateMachine.onUserPause(player.currentPosition)?.let { emitEvent(it) }
                    }
                } else {
                    stateMachine.onSystemPlayWhenReadyChange()
                }

                if (playWhenReady && player.isPlaying) {
                    stateMachine.tryStartCurrentTrack()?.let { emitEvent(it) }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    stateMachine.tryStartCurrentTrack()?.let { emitEvent(it) }
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK &&
                    oldPosition.mediaItemIndex == newPosition.mediaItemIndex
                ) {
                    stateMachine.onSeekWithinTrack(newPosition.positionMs)?.let { emitEvent(it) }
                }
            }
        })

        val builder = MediaSession.Builder(this, forwarding)
        sessionActivity?.let { builder.setSessionActivity(it) }
        mediaSession = builder.build()
    }

    private fun extractPlaybackItemId(item: MediaItem?): String? = item?.mediaId

    private fun extractTrackId(item: MediaItem?): String? =
        item?.mediaMetadata?.extras?.getString("trackId")

    private fun emitEvent(event: com.example.muse.core.model.PlayEvent) {
        eventListener?.onPlayEvent(event)
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? = mediaSession

    override fun onDestroy() {
        stateMachine.onStop()
        mediaSession?.let { session ->
            session.release()
            mediaSession = null
        }
        exoPlayer?.release()
        exoPlayer = null
        super.onDestroy()
    }
}