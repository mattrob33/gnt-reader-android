package com.mattrobertson.greek.reader.audio.playback

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import androidx.media3.session.SessionResult.RESULT_SUCCESS
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.mattrobertson.greek.reader.audio.R
import com.mattrobertson.greek.reader.audio.data.AudioSettings
import com.mattrobertson.greek.reader.audio.data.AudioUrlProvider
import com.mattrobertson.greek.reader.audio.data.Pronunciation
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.getBookTitleLocalized
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class AudioService: MediaSessionService() {

    @Inject lateinit var urlProvider: AudioUrlProvider

    @Inject lateinit var audioSettings: AudioSettings

    lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    private var pronunciation = Pronunciation.Modern

    private var currentRef: VerseRef? = null

    private val artwork by lazy {
        val drawable = resources.getDrawable(R.drawable.ic_launcher)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.toByteArray()
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onGetSession(controllerInfo: ControllerInfo) = mediaSession

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this)
            .setSeekBackIncrementMs(10_000)
            .setSeekForwardIncrementMs(10_000)
            .build()

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MediaSessionCallback())
            .build()

        serviceScope.launch {
            audioSettings.pronunciation.collect {
                if (pronunciation != it) {
                    player.stop()
                    pronunciation = it
                    if (player.isPlaying) {
                        currentRef?.let { currentRef ->
                            player.play(currentRef)
                        }
                    }
                }
            }
        }

        serviceScope.launch {
            audioSettings.playbackSpeed.collect { playbackSpeed ->
                player.setPlaybackSpeed(playbackSpeed)
            }
        }
    }

    override fun onDestroy() {
        player.release()
        mediaSession.release()
        serviceJob.cancel()
        super.onDestroy()
    }

    private fun ExoPlayer.play(ref: VerseRef) {

        currentRef = ref

        val humanReadableRef = "${getBookTitleLocalized(ref.book)} ${ref.chapter}"

        val url = urlProvider.getDefaultUrl(pronunciation, ref)

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(humanReadableRef)
                    .setDisplayTitle(humanReadableRef)
                    .setArtist("Greek New Testament")
                    .setSubtitle("Greek New Testament")
                    .setArtworkData(artwork, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                    .build()
            )
            .build()
        setMediaItem(mediaItem)
        playWhenReady = true
        prepare()
    }

    private inner class MediaSessionCallback: MediaSession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: ControllerInfo
        ): ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val sessionCommands = connectionResult.availableSessionCommands
                .buildUpon()
                .add(SessionCommand("PlayRef", bundleOf()))
                .build()
            return ConnectionResult.accept(sessionCommands, connectionResult.availablePlayerCommands)
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            when (customCommand.customAction) {
                "PlayRef" -> player.play(args.get("ref") as VerseRef)
            }
            return Futures.immediateFuture(SessionResult(RESULT_SUCCESS))
        }
    }

}