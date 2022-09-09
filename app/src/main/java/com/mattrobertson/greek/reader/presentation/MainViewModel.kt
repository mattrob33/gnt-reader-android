package com.mattrobertson.greek.reader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattrobertson.greek.reader.audio.PlaybackState.*
import com.mattrobertson.greek.reader.audio.data.AudioNarrator
import com.mattrobertson.greek.reader.audio.data.AudioSettings
import com.mattrobertson.greek.reader.audio.playback.AudioServiceConnection
import com.mattrobertson.greek.reader.db.api.repo.ConcordanceRepo
import com.mattrobertson.greek.reader.db.api.repo.GlossesRepo
import com.mattrobertson.greek.reader.db.api.repo.VerseRepo
import com.mattrobertson.greek.reader.db.api.repo.VocabRepo
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.settings.SettingsState
import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val verseRepo: VerseRepo,
    val glossesRepo: GlossesRepo,
    val concordanceRepo: ConcordanceRepo,
    val vocabRepo: VocabRepo,
    private val audioService: AudioServiceConnection,
    private val audioSettings: AudioSettings,
    _settings: Settings
): ViewModel() {

    private val _currentRef = MutableStateFlow(VerseRef(Book.MATTHEW, 1, 1))
    val currentRef = _currentRef.asStateFlow()

    val settings = _settings.settingsState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SettingsState.default
    )

    val audioPlaybackState = audioService.playbackState

    val audioPlaybackSpeed = audioSettings.playbackSpeed
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            1.0f
        )

    val audioNarrator = audioSettings.narrator
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            AudioNarrator.ErasmianPhemister
        )

    fun onChangeVerseRef(ref: VerseRef) {
        _currentRef.update { ref }
        if (audioPlaybackState.value == Playing) {
            audioService.stop()
        }
    }

    fun onTapPlayPauseAudio() {
        when (audioService.playbackState.value) {
            Stopped -> audioService.play(currentRef.value)
            Paused -> audioService.resume()
            Playing -> audioService.pause()
            Buffering -> {} // ignore
        }
    }

    fun onTapSkipBack() {
        audioService.skipBack()
    }

    fun onTapSkipForward() {
        audioService.skipForward()
    }

    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            audioSettings.setPlaybackSpeed(speed)
        }
    }

    fun setNarrator(narrator: AudioNarrator) {
        viewModelScope.launch {
            audioSettings.setNarrator(narrator)
        }
    }
}