package com.mattrobertson.greek.reader.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.SblGntApplication

sealed class BottomNavItem(val route: String, @StringRes val label: Int, val icon: ImageVector) {
    object Contents : BottomNavItem("contents", R.string.contents, Icons.Rounded.List)
    object Vocab : BottomNavItem("vocab", R.string.vocab, ImageVector.vectorResource(null, SblGntApplication.context.resources, R.drawable.ic_vocab))
    object Audio : BottomNavItem("audio", R.string.audio, Icons.Rounded.Speaker)
    object Settings : BottomNavItem("settings", R.string.settings, Icons.Rounded.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Contents,
    BottomNavItem.Vocab,
    BottomNavItem.Audio,
    BottomNavItem.Settings
)