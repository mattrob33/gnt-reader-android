package com.mattrobertson.greek.reader.audio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.audio.PlaybackState
import com.mattrobertson.greek.reader.audio.PlaybackState.*
import com.mattrobertson.greek.reader.ui.lib.HSpacer
import com.mattrobertson.greek.reader.ui.lib.MaxWidthColumn
import com.mattrobertson.greek.reader.ui.lib.MaxWidthRow
import com.mattrobertson.greek.reader.ui.lib.VSpacer
import com.mattrobertson.greek.reader.ui.theme.AppTheme

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable private fun AudioPanel_PreviewLight() {
    AppTheme(darkTheme = false) {
        AudioPanel(onDismiss = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0x222222)
@Composable private fun AudioPanel_PreviewDark() {
    AppTheme(darkTheme = true) {
        AudioPanel(onDismiss = {})
    }
}

@Composable fun AudioPanel(
    onDismiss: () -> Unit
) {

    val playbackState by remember { mutableStateOf(Stopped) }

    MaxWidthColumn(
        modifier = Modifier.background(MaterialTheme.colors.background).clickable {}
    ) {

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(end = 8.dp)
        ) {
            Icon(
                Icons.Rounded.ExpandMore,
                "Close",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }

        MaxWidthRow {
            BackButton()
            HSpacer(20.dp)
            PlayPauseButton(playbackState)
            HSpacer(20.dp)
            ForwardButton()
        }

        VSpacer(10.dp)
        
        NarratorToggle()

        VSpacer(20.dp)
    }
}

@Composable private fun PlayPauseButton(
    playbackState: PlaybackState
) {
    when (playbackState) {
        Paused, Stopped -> PlayButton()
        Playing -> PauseButton()
        Buffering -> BufferingIcon()
    }
}

@Composable private fun PlayButton() {
    Icon(
        Icons.Rounded.PlayArrow,
        "Play button",
        tint = MaterialTheme.colors.primary,
        modifier = Modifier.size(72.dp)
    )
}

@Composable private fun PauseButton() {
    Icon(
        Icons.Rounded.Pause,
        "Pause button",
        tint = MaterialTheme.colors.primary,
        modifier = Modifier.size(64.dp)
    )
}

@Composable private fun BufferingIcon() {
    CircularProgressIndicator(
        modifier = Modifier.size(72.dp),
        color = MaterialTheme.colors.primary
    )
}

@Composable private fun BackButton() {
    IconButton(
        onClick = {}
    ) {
        Icon(
            Icons.Rounded.Replay10,
            "Skip back",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable private fun ForwardButton() {
    IconButton(
        onClick = {}
    ) {
        Icon(
            Icons.Rounded.Forward10,
            "Skip forward",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable private fun NarratorToggle() {
    var isChecked by remember { mutableStateOf(false) }
    
    val switchColor = MaterialTheme.colors.primary

    val trackColor = switchColor.copy(
        red = switchColor.red * 0.7f,
        green = switchColor.green * 0.7f,
        blue = switchColor.blue * 0.7f
    )

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor= switchColor,
        checkedTrackColor= trackColor,
        checkedTrackAlpha = 0.3f,
        uncheckedThumbColor= switchColor,
        uncheckedTrackColor= trackColor,
        uncheckedTrackAlpha = 0.3f
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        NarratorToggleLabel(
            text = "Erasmian",
            isActive = !isChecked,
            onClick = { isChecked = false }
        )

        Switch(
            checked = isChecked,
            onCheckedChange = { checked -> isChecked = checked },
            colors = switchColors,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .align(Alignment.CenterVertically)
        )

        NarratorToggleLabel(
            text = "Modern",
            isActive = isChecked,
            onClick = { isChecked = true }
        )
    }
}

@Composable private fun NarratorToggleLabel(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val activeTextColor = MaterialTheme.colors.primary
    val inActiveTextColor = Color.Gray

    Text(
        text,
        style = MaterialTheme.typography.caption,
        fontSize = 16.sp,
        color = if (isActive) { activeTextColor } else { inActiveTextColor },
        modifier = Modifier.clickable { onClick() }.padding(10.dp)
    )
}