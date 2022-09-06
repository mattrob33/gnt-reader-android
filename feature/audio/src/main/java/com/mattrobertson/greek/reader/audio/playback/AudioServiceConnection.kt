package com.mattrobertson.greek.reader.audio.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.os.bundleOf
import androidx.media3.common.Player
import androidx.media3.common.Player.*
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.mattrobertson.greek.reader.audio.PlaybackState
import com.mattrobertson.greek.reader.audio.PlaybackState.*
import com.mattrobertson.greek.reader.verseref.VerseRef
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val sessionToken = SessionToken(context, ComponentName(context, AudioService::class.java))

    private val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

    private val mediaController: MediaController
        get() = controllerFuture.get()

    private val listener = Listener()

    private var _state = MutableStateFlow(Stopped)
    var playbackState: StateFlow<PlaybackState> = _state

    init {
        controllerFuture.addListener({
                mediaController.addListener(listener)
             },
            MoreExecutors.directExecutor()
        )
    }

    fun play(ref: VerseRef) {
        mediaController.sendCustomCommand(
            SessionCommand("PlayRef", bundleOf()),
            bundleOf("ref" to ref)
        )
    }

    fun resume() = mediaController.play()
    fun pause() = mediaController.pause()
    fun stop() = mediaController.stop()

    fun skipBack() = mediaController.seekBack()
    fun skipForward() = mediaController.seekForward()

    fun release() = mediaController.release()

    private inner class Listener: Player.Listener {
        override fun onEvents(player: Player, events: Events) {
            _state.value = when {
                player.isPlaying -> Playing
                else -> {
                    when (player.playbackState) {
                        STATE_IDLE -> Stopped
                        STATE_BUFFERING -> Buffering
                        else -> Paused
                    }
                }
            }
        }
    }
}