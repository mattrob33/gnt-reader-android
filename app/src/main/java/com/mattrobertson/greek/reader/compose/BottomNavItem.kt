package com.mattrobertson.greek.reader.compose

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.ui.graphics.vector.ImageVector
import com.mattrobertson.greek.reader.R

sealed class BottomNavItem(val route: String, @StringRes val label: Int, val icon: ImageVector) {
    object Vocab : BottomNavItem("vocab", R.string.vocab, Icons.Rounded.List)
    object Audio : BottomNavItem("audio", R.string.audio, Icons.Rounded.Speaker)
    object Settings : BottomNavItem("settings", R.string.settings, Icons.Rounded.Settings)
}