package com.mattrobertson.greek.reader.settings

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.ui.settings.FontFamily

data class SettingsState(
    val font: FontFamily,
    val fontSize: TextUnit,
    val lineSpacing: TextUnit,
    val showVerseNumbers: Boolean,
    val versesOnNewLines: Boolean
) {
    companion object {
        val default = SettingsState(
            FontFamily.default,
            16.sp,
            24.sp,
            showVerseNumbers = true,
            versesOnNewLines = false
        )
    }
}
