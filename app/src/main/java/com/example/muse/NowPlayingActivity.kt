package com.example.muse

import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.muse.core.media.PlaybackService
import com.example.muse.databinding.ActivityNowPlayingBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class NowPlayingActivity : ComponentActivity() {

    private lateinit var binding: ActivityNowPlayingBinding
    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var listenerAttached = false
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false

    private val progressUpdater = object : Runnable {
        override fun run() {
            val c = controller ?: return
            if (!isUserSeeking) {
                val pos = c.currentPosition.coerceAtLeast(0L)
                val dur = c.duration.coerceAtLeast(0L)
                binding.seekBar.max = dur.toInt().coerceAtLeast(1)
                binding.seekBar.progress = pos.toInt().coerceIn(0, binding.seekBar.max)
                binding.currentTime.text = formatTime(pos)
                binding.totalTime.text = formatTime(dur)
            }
            handler.postDelayed(this, 500L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MUSE_Xml)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNowPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener { finish() }
        binding.btnPlayPause.setOnClickListener {
            val c = controller ?: return@setOnClickListener
            if (c.isPlaying) c.pause() else c.play()
        }
        binding.btnPrevious.setOnClickListener { controller?.seekToPreviousMediaItem() }
        binding.btnNext.setOnClickListener { controller?.seekToNextMediaItem() }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) binding.currentTime.text = formatTime(progress.toLong())
            }
            override fun onStartTrackingTouch(sb: SeekBar) { isUserSeeking = true }
            override fun onStopTrackingTouch(sb: SeekBar) {
                isUserSeeking = false
                controller?.seekTo(sb.progress.toLong())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val future = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture = future

        future.addListener({
            // Проверяем: это всё ещё наш Future, Activity в STARTED, и не уничтожена
            if (controllerFuture !== future ||
                !lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) ||
                isDestroyed || isFinishing
            ) {
                if (controllerFuture !== future) {
                    MediaController.releaseFuture(future)
                }
                return@addListener
            }
            try {
                val c = future.get()
                controller = c
                if (!listenerAttached) {
                    c.addListener(playerListener)
                    listenerAttached = true
                }
                updateUi()
                handler.post(progressUpdater)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onStop() {
        handler.removeCallbacks(progressUpdater)
        if (listenerAttached) {
            controller?.removeListener(playerListener)
            listenerAttached = false
        }
        controller = null
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        super.onStop()
    }

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { updateUi() }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            binding.btnPlayPause.setImageResource(
                if (isPlaying) android.R.drawable.ic_media_pause
                else android.R.drawable.ic_media_play
            )
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) updateUi()
        }
    }

    private fun updateUi() {
        val c = controller ?: return
        val item = c.currentMediaItem ?: run {
            binding.title.text = "—"
            binding.artist.text = "—"
            binding.artwork.setImageResource(R.drawable.placeholder_artwork)
            binding.seekBar.progress = 0
            binding.currentTime.text = "0:00"
            binding.totalTime.text = "0:00"
            return
        }
        binding.title.text = item.mediaMetadata.title ?: "—"
        binding.artist.text = item.mediaMetadata.artist ?: "—"

        val artUri = item.mediaMetadata.artworkUri?.toString()
        Glide.with(binding.artwork)
            .load(artUri)
            .placeholder(R.drawable.placeholder_artwork)
            .error(R.drawable.placeholder_artwork)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop()
            .into(binding.artwork)
    }

    private fun formatTime(ms: Long): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        return "%d:%02d".format(totalSec / 60, totalSec % 60)
    }
}