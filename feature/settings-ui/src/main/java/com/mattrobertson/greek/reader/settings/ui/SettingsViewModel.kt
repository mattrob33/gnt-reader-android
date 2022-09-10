package com.mattrobertson.greek.reader.settings.ui

import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattrobertson.greek.reader.settings.SettingsStore
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.ui.settings.FontFamily
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStore
): ViewModel() {

    val settings = settingsStore.settings.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Settings.default
    )

    fun setFont(font: FontFamily) = settingsStore.setFont(font)

    fun setFontSize(sp: TextUnit) = settingsStore.setFontSize(sp)

    fun setLineSpacingMultiplier(multiplier: Float) = settingsStore.setLineSpacingMultiplier(multiplier)

    fun setShowVerseNumbers(show: Boolean) = settingsStore.setShowVerseNumbers(show)

    fun setVersesOnNewLines(show: Boolean) = settingsStore.setVersesOnNewLines(show)
}