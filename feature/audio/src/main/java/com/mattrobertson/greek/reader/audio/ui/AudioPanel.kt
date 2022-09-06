package com.mattrobertson.greek.reader.audio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.mattrobertson.greek.reader.ui.lib.*
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable private fun AudioPanel_PreviewLight() {
    AppTheme(darkTheme = false) {
        AudioPanel(
            playbackState = MutableStateFlow(Stopped),
            onDismiss = {},
            onTapPlayPause = {},
            onTapSkipBack = {},
            onTapSkipForward = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0x222222)
@Composable private fun AudioPanel_PreviewDark() {
    AppTheme(darkTheme = true) {
        AudioPanel(
            playbackState = MutableStateFlow(Stopped),
            onDismiss = {},
            onTapPlayPause = {},
            onTapSkipBack = {},
            onTapSkipForward = {}
        )
    }
}

@Composable fun AudioPanel(
    playbackState: StateFlow<PlaybackState>,
    onDismiss: () -> Unit,
    onTapPlayPause: () -> Unit,
    onTapSkipBack: () -> Unit,
    onTapSkipForward: () -> Unit
) {

    val state by playbackState.collectAsState()

    MaxWidthColumn(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .noRippleClickable {}
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
            BackButton(onTapSkipBack)
            HSpacer(20.dp)
            PlayPauseButton(state, onTapPlayPause)
            HSpacer(20.dp)
            ForwardButton(onTapSkipForward)
        }

        VSpacer(4.dp)
        
        NarratorToggle()

        VSpacer(24.dp)
    }
}

@Composable private fun PlayPauseButton(
    playbackState: PlaybackState,
    onTapPlayPause: () -> Unit
) {
    when (playbackState) {
        Paused, Stopped -> PlayButton(onTapPlayPause)
        Playing -> PauseButton(onTapPlayPause)
        Buffering -> BufferingIcon()
    }
}

@Composable private fun PlayButton(onTap: () -> Unit) {
    Icon(
        Icons.Rounded.PlayArrow,
        "Play button",
        tint = MaterialTheme.colors.primary,
        modifier = Modifier
            .size(100.dp)
            .noRippleClickable { onTap() }
    )
}

@Composable private fun PauseButton(onTap: () -> Unit) {
    Icon(
        Icons.Rounded.Pause,
        "Pause button",
        tint = MaterialTheme.colors.primary,
        modifier = Modifier
            .size(100.dp)
            .padding(12.dp)
            .noRippleClickable { onTap() }
    )
}

@Composable private fun BufferingIcon() {
    CircularProgressIndicator(
        modifier = Modifier.size(100.dp).padding(24.dp),
        color = MaterialTheme.colors.primary
    )
}

@Composable private fun BackButton(onTap: () -> Unit) {
    IconButton(onClick = onTap) {
        Icon(
            Icons.Rounded.Replay10,
            "Skip back",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable private fun ForwardButton(onTap: () -> Unit) {
    IconButton(onClick = onTap) {
        Icon(
            Icons.Rounded.Forward10,
            "Skip forward",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(40.dp)
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
        modifier = Modifier
            .noRippleClickable { onClick() }
            .padding(10.dp)
    )
}