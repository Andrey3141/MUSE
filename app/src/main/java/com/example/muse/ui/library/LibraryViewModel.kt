package com.example.muse.ui.library

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.muse.MuseApplication
import com.example.muse.core.model.Track
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as MuseApplication
    private val context = application.applicationContext

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    val tracks: StateFlow<List<Track>> = app.database.trackDao()
        .observeAll()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun requiredPermission(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    fun hasAudioPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, requiredPermission()) ==
                PackageManager.PERMISSION_GRANTED

    fun scanLibrary() {
        if (_scanState.value is ScanState.Scanning) return

        if (!hasAudioPermission()) {
            _scanState.value = ScanState.Error("Нет разрешения на чтение аудио")
            return
        }

        _scanState.value = ScanState.Scanning

        viewModelScope.launch {
            try {
                val result = app.trackRepository.scanAndMerge()
                if (!result.isComplete) {
                    _scanState.value = ScanState.Error(
                        result.errorMessage ?: "Сканирование неполное. Повторите позже."
                    )
                    return@launch
                }
                app.trackRepository.syncLibraryStatus(result.scannedStableKeys)
                _scanState.value = ScanState.Done(result.newCount)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.message ?: "Ошибка сканирования")
            }
        }
    }

    sealed interface ScanState {
        data object Idle : ScanState
        data object Scanning : ScanState
        data class Done(val newTracks: Int) : ScanState
        data class Error(val message: String) : ScanState
    }
}