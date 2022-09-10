package com.mattrobertson.greek.reader.settings

import com.mattrobertson.greek.reader.settings.SettingsStore.Keys.Font
import com.mattrobertson.greek.reader.settings.SettingsStore.Keys.FontSize
import com.mattrobertson.greek.reader.settings.SettingsStore.Keys.LineSpacing
import com.mattrobertson.greek.reader.settings.SettingsStore.Keys.ShowVerseNumbers
import com.mattrobertson.greek.reader.settings.SettingsStore.Keys.VersesOnNewLines
import android.content.Context
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mattrobertson.greek.reader.ui.settings.FontFamily
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext appContext: Context
) {

    private val dataStore = appContext.appSettingsDataStore

    val font: Flow<FontFamily> = getDataStoreItem(Font, FontFamily.default.id).map { FontFamily.fromId(it) }
    fun setFont(font: FontFamily) = setDataStoreItem(Font, font.id)

    val fontSize: Flow<TextUnit> = getDataStoreItem(FontSize, 16f).map { it.sp }
    fun setFontSize(sp: TextUnit) = setDataStoreItem(FontSize, sp.value)

    val lineSpacing: Flow<Float> = getDataStoreItem(LineSpacing, 1.5f).map { it }
    fun setLineSpacingMultiplier(multiplier: Float) = setDataStoreItem(LineSpacing, multiplier)

    val showVerseNumbers: Flow<Boolean> = getDataStoreItem(ShowVerseNumbers, true)
    fun setShowVerseNumbers(show: Boolean) = setDataStoreItem(ShowVerseNumbers, show)

    val versesOnNewLines: Flow<Boolean> = getDataStoreItem(VersesOnNewLines, false)
    fun setVersesOnNewLines(show: Boolean) = setDataStoreItem(VersesOnNewLines, show)

    val settings: Flow<Settings> =
        combine(font, fontSize, lineSpacing, showVerseNumbers, versesOnNewLines) { font, fontSize, lineSpacing, showVerseNumbers, versesOnNewLines ->
            Settings(font, fontSize, lineSpacing, showVerseNumbers, versesOnNewLines)
        }


    private fun <T> getDataStoreItem(key: Preferences.Key<T>, default: T): Flow<T> {
        return dataStore.data.map { prefs -> prefs[key] ?: default }
    }

    private fun <T> setDataStoreItem(key: Preferences.Key<T>, value: T) {
        GlobalScope.launch {
            dataStore.edit { prefs -> prefs[key] = value }
        }
    }

    object Keys {
        val Font = stringPreferencesKey("font_family")
        val FontSize = floatPreferencesKey("font_size")
        val LineSpacing = floatPreferencesKey("line_spacing")
        val ShowVerseNumbers = booleanPreferencesKey("show_verse_numbers")
        val VersesOnNewLines = booleanPreferencesKey("verses_on_new_lines")
    }
}

private val Context.appSettingsDataStore by preferencesDataStore("app_settings")