package com.example.muse

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.muse.core.media.MediaItemFactory
import com.example.muse.databinding.ActivityMainBinding
import com.example.muse.ui.library.LibraryViewModel
import com.example.muse.ui.library.TrackAdapter
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LibraryViewModel
    private lateinit var adapter: TrackAdapter

    private var controller: MediaController? = null
    private var controllerListenerAttached = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            binding.permissionMessage.visibility = android.view.View.GONE
            viewModel.scanLibrary()
        } else {
            binding.permissionMessage.visibility = android.view.View.VISIBLE
            binding.permissionMessage.text =
                "Доступ к аудио не предоставлен. Нажмите «Сканировать музыку», чтобы запросить разрешение снова. Если система больше не показывает запрос, включите доступ в настройках приложения."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MUSE_Xml)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LibraryViewModel::class.java]

        adapter = TrackAdapter { track, position ->
            playTrack(track, position)
        }
        binding.trackList.layoutManager = LinearLayoutManager(this)
        binding.trackList.adapter = adapter
        binding.trackList.setHasFixedSize(true)
        (binding.trackList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.scanButton.setOnClickListener {
            if (viewModel.hasAudioPermission()) {
                binding.permissionMessage.visibility = android.view.View.GONE
                viewModel.scanLibrary()
            } else {
                permissionLauncher.launch(viewModel.requiredPermission())
            }
        }

        setupPlayerPanel()

        lifecycleScope.launch {
            when (val result = UpdateChecker.checkForUpdate(applicationContext)) {
                is UpdateCheckResult.UpdateAvailable -> {
                    UpdateDialog.show(this@MainActivity, result.info)
                }
                else -> { /* тихо игнорируем */ }
            }
        }

        val app = application as MuseApplication
        app.connectToPlaybackService()
        app.controllerFuture?.addListener({
            if (isDestroyed || isFinishing) return@addListener
            try {
                val c = app.controllerFuture?.get() ?: return@addListener
                controller = c
                if (!controllerListenerAttached) {
                    c.addListener(playerListener)
                    controllerListenerAttached = true
                }
                syncPlayPauseIcon()
                updatePlayerPanel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onDestroy() {
        if (controllerListenerAttached) {
            controller?.removeListener(playerListener)
            controllerListenerAttached = false
        }
        controller = null
        super.onDestroy()
    }

    private fun setupPlayerPanel() {
        binding.btnPlayPause.setOnClickListener {
            val c = controller ?: return@setOnClickListener
            if (c.isPlaying) c.pause() else c.play()
        }
        binding.btnPrevious.setOnClickListener {
            controller?.seekToPreviousMediaItem()
        }
        binding.btnNext.setOnClickListener {
            controller?.seekToNextMediaItem()
        }
    }

    private fun playTrack(track: com.example.muse.core.model.Track, position: Int) {
        val c = controller
        if (c == null) {
            Toast.makeText(this, "Плеер ещё подключается…", Toast.LENGTH_SHORT).show()
            return
        }
        val tracks = viewModel.tracks.value
        if (tracks.isEmpty()) return

        val result = MediaItemFactory.createAll(tracks)
        if (result.mediaItems.isEmpty()) {
            Toast.makeText(this, "Нет треков с доступным источником", Toast.LENGTH_LONG).show()
            return
        }

        // Индекс выбранного трека в исходном списке
        val originalIndex = tracks.indexOfFirst { it.id == track.id }
        if (originalIndex < 0) return

        // Если выбранный трек недоступен — не подменять его другим
        val filteredIndex = result.sourceIndices.indexOf(originalIndex)
        if (filteredIndex < 0) {
            Toast.makeText(this, "Этот трек недоступен: нет ни оригинала, ни копии", Toast.LENGTH_LONG).show()
            return
        }

        try {
            c.setMediaItems(result.mediaItems, filteredIndex, 0L)
            c.prepare()
            c.play()
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Ошибка: ForwardingSimpleBasePlayer не поддерживает setMediaItems. Требуется миграция на SimpleBasePlayer.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Не удалось запустить воспроизведение", Toast.LENGTH_SHORT).show()
        }
    }

    private val playerListener = object : Player.Listener {

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updatePlayerPanel()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            binding.btnPlayPause.setImageResource(
                if (isPlaying) android.R.drawable.ic_media_pause
                else android.R.drawable.ic_media_play
            )
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                updatePlayerPanel()
                syncPlayPauseIcon()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            error.printStackTrace()
            val message = when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "Файл не найден"
                PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "Ошибка доступа к файлу"
                PlaybackException.ERROR_CODE_DECODING_FAILED -> "Не удалось декодировать аудио"
                else -> "Ошибка воспроизведения: ${error.errorCodeName}"
            }
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            updatePlayerPanel()
        }
    }

    private fun syncPlayPauseIcon() {
        val c = controller ?: return
        binding.btnPlayPause.setImageResource(
            if (c.isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun updatePlayerPanel() {
        val c = controller ?: return
        val item = c.currentMediaItem
        if (item == null) {
            binding.playerPanel.visibility = android.view.View.GONE
            return
        }
        binding.playerPanel.visibility = android.view.View.VISIBLE
        binding.playerTitle.text = item.mediaMetadata.title ?: "—"
        binding.playerArtist.text = item.mediaMetadata.artist ?: "—"

        val artUri = item.mediaMetadata.artworkUri?.toString()
        Glide.with(binding.playerArtwork)
            .load(artUri)
            .placeholder(R.drawable.placeholder_artwork)
            .error(R.drawable.placeholder_artwork)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop()
            .into(binding.playerArtwork)
    }

    private fun updateEmptyState(
        tracks: List<com.example.muse.core.model.Track>,
        state: LibraryViewModel.ScanState
    ) {
        val isScanning = state is LibraryViewModel.ScanState.Scanning
        binding.emptyState.visibility =
            if (tracks.isEmpty() && !isScanning) android.view.View.VISIBLE
            else android.view.View.GONE
    }

    private fun renderScanState(state: LibraryViewModel.ScanState) {
        when (state) {
            is LibraryViewModel.ScanState.Idle -> {
                binding.scanButton.isEnabled = true
                binding.scanButton.text = "Сканировать музыку"
                binding.scanProgress.visibility = android.view.View.GONE
                binding.scanError.visibility = android.view.View.GONE
            }
            is LibraryViewModel.ScanState.Scanning -> {
                binding.scanButton.isEnabled = false
                binding.scanButton.text = "Сканирование…"
                binding.scanProgress.visibility = android.view.View.VISIBLE
                binding.scanError.visibility = android.view.View.GONE
            }
            is LibraryViewModel.ScanState.Done -> {
                binding.scanButton.isEnabled = true
                binding.scanButton.text =
                    if (state.newTracks > 0)
                        "Добавлено: ${state.newTracks}. Обновить"
                    else
                        "Обновить библиотеку"
                binding.scanProgress.visibility = android.view.View.GONE
                binding.scanError.visibility = android.view.View.GONE
            }
            is LibraryViewModel.ScanState.Error -> {
                binding.scanButton.isEnabled = true
                binding.scanButton.text = "Повторить"
                binding.scanProgress.visibility = android.view.View.GONE
                binding.scanError.visibility = android.view.View.VISIBLE
                binding.scanError.text = "Ошибка: ${state.message}"
            }
        }
    }
}