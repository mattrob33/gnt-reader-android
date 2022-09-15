package com.mattrobertson.greek.reader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattrobertson.greek.reader.audio.PlaybackState.*
import com.mattrobertson.greek.reader.audio.data.AudioSettings
import com.mattrobertson.greek.reader.audio.data.Pronunciation
import com.mattrobertson.greek.reader.audio.playback.AudioServiceConnection
import com.mattrobertson.greek.reader.db.api.repo.ConcordanceRepo
import com.mattrobertson.greek.reader.db.api.repo.GlossesRepo
import com.mattrobertson.greek.reader.db.api.repo.VerseRepo
import com.mattrobertson.greek.reader.db.api.repo.VocabRepo
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.settings.SettingsStore
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
    _settings: SettingsStore
): ViewModel() {

    private val _currentRef = MutableStateFlow(VerseRef(Book.MATTHEW, 1, 1))
    val currentRef = _currentRef.asStateFlow()

    val settings = _settings.settings.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Settings.default
    )

    val audioPlaybackState = audioService.playbackState

    val audioPlaybackSpeed = audioSettings.playbackSpeed
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            1.0f
        )

    val audioPronunciation = audioSettings.pronunciation
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            Pronunciation.Modern
        )

    fun onChangeVerseRef(ref: VerseRef) {
        _currentRef.update { ref }
        when (audioPlaybackState.value) {
            Playing, Paused, Buffering -> audioService.stop()
            else -> {}
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

    fun setPronunciation(pronunciation: Pronunciation) {
        viewModelScope.launch {
            audioSettings.setPronunciation(pronunciation)
        }
    }
}