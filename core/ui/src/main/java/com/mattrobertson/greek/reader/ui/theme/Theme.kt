package com.mattrobertson.greek.reader.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Blue200,
    primaryVariant = Blue800,
    secondary = Blue200,
    secondaryVariant = Blue200,
    background = Color(0xFF222222),
    surface = Color(0xFF222222)
)

private val LightColorPalette = lightColors(
    primary = Blue800,
    primaryVariant = Blue800,
    secondary = Blue800,
    secondaryVariant = Blue800,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}