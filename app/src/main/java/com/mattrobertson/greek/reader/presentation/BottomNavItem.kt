package com.mattrobertson.greek.reader.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.SblGntApplication

sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object Contents : BottomNavItem("contents", Icons.Rounded.FormatListBulleted)
    object Vocab : BottomNavItem("vocab", ImageVector.vectorResource(null, SblGntApplication.context.resources, R.drawable.ic_vocab))
    object Audio : BottomNavItem("audio", Icons.Rounded.VolumeUp)
    object Settings : BottomNavItem("settings", Icons.Rounded.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Contents,
    BottomNavItem.Vocab,
    BottomNavItem.Audio,
    BottomNavItem.Settings
)