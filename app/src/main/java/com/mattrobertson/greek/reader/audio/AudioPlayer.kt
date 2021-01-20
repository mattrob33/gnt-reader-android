package com.mattrobertson.greek.reader.audio

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import com.mattrobertson.greek.reader.SblGntApplication
import com.mattrobertson.greek.reader.model.VerseRef
import javax.inject.Inject

class AudioPlayer @Inject constructor(
	private val connectivityManager: ConnectivityManager
): IAudioPlayer {

	var audioPrepared: AudioPrepared? = null

	private var player = MediaPlayer()

	private var state = AudioPlaybackState.STOPPED

	private var playProgress = 0

	private var playingRef: VerseRef? = null

	override fun playChapter(ref: VerseRef) {
		if (!isNetworkConnected()) return

		playingRef = ref

		try {
			state = AudioPlaybackState.PREPARING

			stop()

			val url = GreekLatinAudio.getUrl(ref)

			player = MediaPlayer.create(SblGntApplication.context, Uri.parse(url))

			player.setAudioStreamType(AudioManager.STREAM_MUSIC)

			player.setOnPreparedListener {
				if (playProgress > 0)
					player.seekTo(playProgress)

				player.start()

				state = AudioPlaybackState.PLAYING
				audioPrepared?.onAudioPrepared()
			}
			player.prepareAsync()
		}
		catch (e: Exception) {
			Log.e("gnt", "Error: ${e.message}")
		}
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
			player.release()
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