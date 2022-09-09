package com.mattrobertson.greek.reader.settings.ui

import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.settings.SettingsState
import com.mattrobertson.greek.reader.ui.settings.FontFamily
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val _settings: Settings
): ViewModel() {

    val settings = _settings.settingsState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SettingsState.default
    )

    fun setFont(font: FontFamily) = _settings.setFont(font)

    fun setFontSize(sp: TextUnit) = _settings.setFontSize(sp)

    fun setLineSpacing(sp: TextUnit) = _settings.setLineSpacing(sp)

    fun setShowVerseNumbers(show: Boolean) = _settings.setShowVerseNumbers(show)

    fun setVersesOnNewLines(show: Boolean) = _settings.setVersesOnNewLines(show)
}