package com.mattrobertson.greek.reader.audio.playback

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
import com.mattrobertson.greek.reader.audio.data.AudioNarrator
import com.mattrobertson.greek.reader.audio.data.AudioUrlProvider
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.getBookTitleLocalized
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioService: MediaSessionService() {

    @Inject lateinit var urlProvider: AudioUrlProvider

    lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    // TODO - get this as an observable from settings
    private var narrator: AudioNarrator = AudioNarrator.ModernSblgnt

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
    }

    override fun onDestroy() {
        player.release()
        mediaSession.release()
        super.onDestroy()
    }

    private fun ExoPlayer.play(ref: VerseRef) {

        val humanReadableRef = "${getBookTitleLocalized(ref.book)} ${ref.chapter}"

        val url = urlProvider.getUrl(narrator, ref)

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(humanReadableRef)
                    .setDisplayTitle(humanReadableRef)
                    .setArtist("Greek New Testament")
                    .setSubtitle("Greek New Testament")
                    .build()
            )
            .build()
        setMediaItem(mediaItem)
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