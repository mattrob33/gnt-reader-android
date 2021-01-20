package com.mattrobertson.greek.reader.audio

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.util.getBookAbbrv
import com.mattrobertson.greek.reader.util.getBookTitle
import javax.inject.Inject

class AudioPlayer @Inject constructor(
	private val connectivityManager: ConnectivityManager
): AudioPlayback {

	companion object {
		private const val urlBase = "http://www.helding.net/greeklatinaudio/greek/"

		private fun getUrl(ref: VerseRef): String {
			val strChap = if (ref.chapter < 10) "0${ref.chapter}" else "" + ref.chapter
			val bookAbbrv = getBookAbbrv(ref.book)
			val bookName = getBookTitle(ref.book)
			return urlBase + "/" + bookName + "/" + bookAbbrv + strChap + "g.mp3"
		}
	}

	var audioPrepared: AudioPrepared? = null

	private var player = MediaPlayer()

	private var state = AudioPlaybackState.STOPPED

	private var playProgress = 0

	private var playingRef: VerseRef? = null

	override fun playChapter(ref: VerseRef) {
		if (!isNetworkConnected()) return

		playingRef = ref

		state = AudioPlaybackState.PREPARING
		stop()

		player.apply {
			setAudioStreamType(AudioManager.STREAM_MUSIC)
			player.setDataSource(
				getUrl(ref)
			)
		}

		player.setOnPreparedListener {
			player = it
			if (playProgress > 0)
				player.seekTo(playProgress)

			player.start()
			state = AudioPlaybackState.PLAYING
			audioPrepared?.onAudioPrepared()
		}
		player.prepareAsync()
	}

	override fun pause() {
		if (player.isPlaying) {
			player.pause()
			state = AudioPlaybackState.PAUSED
			playProgress = player.currentPosition
		}
	}

	override fun stop() {
		if (player.isPlaying) {
			player.stop()
			state = AudioPlaybackState.STOPPED
			playProgress = 0
		}
	}

	override fun restart() {
		playingRef?.let {
			stop()
			playChapter(it)
		}
	}

	override fun getState(): AudioPlaybackState = state

	private fun isNetworkConnected(): Boolean {
		return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
	}
}