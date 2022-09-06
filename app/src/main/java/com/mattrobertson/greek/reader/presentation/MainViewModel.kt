package com.mattrobertson.greek.reader.presentation

import androidx.lifecycle.ViewModel
import com.mattrobertson.greek.reader.audio.PlaybackState.*
import com.mattrobertson.greek.reader.audio.playback.AudioServiceConnection
import com.mattrobertson.greek.reader.db.api.repo.ConcordanceRepo
import com.mattrobertson.greek.reader.db.api.repo.GlossesRepo
import com.mattrobertson.greek.reader.db.api.repo.VerseRepo
import com.mattrobertson.greek.reader.db.api.repo.VocabRepo
import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val verseRepo: VerseRepo,
    val glossesRepo: GlossesRepo,
    val concordanceRepo: ConcordanceRepo,
    val vocabRepo: VocabRepo,
    private val audioService: AudioServiceConnection
): ViewModel() {

    private val _currentRef = MutableStateFlow(VerseRef(Book.MATTHEW, 1, 1))
    val currentRef = _currentRef.asStateFlow()

    val audioPlaybackState = audioService.playbackState

    fun onChangeVerseRef(ref: VerseRef) {
        _currentRef.update { ref }
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
}