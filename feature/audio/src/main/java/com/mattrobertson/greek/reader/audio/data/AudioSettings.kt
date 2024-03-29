package com.mattrobertson.greek.reader.audio.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioSettings @Inject constructor(
    @ApplicationContext appContext: Context
) {
    private val dataStore = appContext.audioSettingsDataStore

    val playbackSpeed: Flow<Float> = dataStore.data.map { prefs -> prefs[Keys.PlaybackSpeed] ?: 1.0f }

    suspend fun setPlaybackSpeed(speed: Float) {
        dataStore.edit { prefs ->
            prefs[Keys.PlaybackSpeed] = speed
        }
    }

    val pronunciation: Flow<Pronunciation> = dataStore.data.map { prefs ->
        prefs[Keys.Pronunciation]?.let { name ->
            return@map Pronunciation.valueOf(name)
        }
        Pronunciation.Modern
    }

    suspend fun setPronunciation(pronunciation: Pronunciation) {
        dataStore.edit { prefs ->
            prefs[Keys.Pronunciation] = pronunciation.name
        }
    }

    private object Keys {
        val PlaybackSpeed = floatPreferencesKey("playback_speed")
        val Narrator = stringPreferencesKey("narrator")
        val Pronunciation = stringPreferencesKey("pronunciation")
    }
}

private val Context.audioSettingsDataStore by preferencesDataStore("audio_settings")