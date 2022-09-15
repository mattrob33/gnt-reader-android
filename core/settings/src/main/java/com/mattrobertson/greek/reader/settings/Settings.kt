package com.mattrobertson.greek.reader.settings

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.ui.settings.FontFamily

data class Settings(
    val font: FontFamily,
    val fontSize: TextUnit,
    val lineSpacing: Float,
    val showVerseNumbers: Boolean,
    val versesOnNewLines: Boolean
) {
    companion object {
        val default = Settings(
            FontFamily.default,
            22.sp,
            1.5f,
            showVerseNumbers = true,
            versesOnNewLines = false
        )
    }
}
