package com.example.muse

import android.app.Application
import android.content.ComponentName
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.muse.core.database.MuseDatabase
import com.example.muse.core.media.PlaybackEventListener
import com.example.muse.core.media.PlaybackService
import com.example.muse.core.model.PlayEvent
import com.example.muse.data.BackupScheduler
import com.example.muse.data.TrackRepository
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MuseApplication : Application(), PlaybackEventListener {

    val database by lazy { MuseDatabase.get(this) }
    val trackRepository by lazy { TrackRepository(this, database) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var controllerFuture: ListenableFuture<MediaController>? = null
        private set

    override fun onCreate() {
        super.onCreate()
        BackupScheduler.scheduleCopy(this)
    }

    fun connectToPlaybackService() {
        if (controllerFuture != null) return
        val token = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(this, token).buildAsync()
    }

    fun releasePlaybackService() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
    }

    override fun onPlayEvent(event: PlayEvent) {
        appScope.launch { trackRepository.persistEvent(event) }
    }
}