package com.mattrobertson.greek.reader.audio

import com.mattrobertson.greek.reader.model.VerseRef

interface IAudioPlayer {

	fun playChapter(ref: VerseRef)

	fun pause()

	fun stop()

	fun restart()

	fun getState(): AudioPlaybackState

}