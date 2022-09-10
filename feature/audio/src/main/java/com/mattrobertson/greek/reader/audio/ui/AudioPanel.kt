package com.mattrobertson.greek.reader.audio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.audio.PlaybackState
import com.mattrobertson.greek.reader.audio.PlaybackState.*
import com.mattrobertson.greek.reader.audio.data.AudioNarrator
import com.mattrobertson.greek.reader.audio.data.AudioNarrator.ErasmianPhemister
import com.mattrobertson.greek.reader.audio.data.AudioNarrator.ModernSblgnt
import com.mattrobertson.greek.reader.ui.lib.*
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable private fun AudioPanel_PreviewLight() {
    AppTheme(darkTheme = false) {
        AudioPanel(
            playbackState = MutableStateFlow(Stopped),
            startingPlaybackSpeedValue = MutableStateFlow(1.0f),
            startingNarratorValue = MutableStateFlow(ErasmianPhemister),
            onDismiss = {},
            onTapPlayPause = {},
            onTapSkipBack = {},
            onTapSkipForward = {},
            onChangePlaybackSpeed = {},
            onChangeNarrator = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0x222222)
@Composable private fun AudioPanel_PreviewDark() {
    AppTheme(darkTheme = true) {
        AudioPanel(
            playbackState = MutableStateFlow(Stopped),
            startingPlaybackSpeedValue = MutableStateFlow(1.0f),
            startingNarratorValue = MutableStateFlow(ErasmianPhemister),
            onDismiss = {},
            onTapPlayPause = {},
            onTapSkipBack = {},
            onTapSkipForward = {},
            onChangePlaybackSpeed = {},
            onChangeNarrator = {},
        )
    }
}

@Composable fun AudioPanel(
    playbackState: StateFlow<PlaybackState>,
    startingPlaybackSpeedValue: StateFlow<Float>,
    startingNarratorValue: StateFlow<AudioNarrator>,
    onDismiss: () -> Unit,
    onTapPlayPause: () -> Unit,
    onTapSkipBack: () -> Unit,
    onTapSkipForward: () -> Unit,
    onChangePlaybackSpeed: (speed: Float) -> Unit,
    onChangeNarrator: (narrator: AudioNarrator) -> Unit,
) {

    val state by playbackState.collectAsState()
    val startingPlaybackSpeed by startingPlaybackSpeedValue.collectAsState()
    val startingNarrator by startingNarratorValue.collectAsState()

    MaxWidthColumn(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .noRippleClickable {}
    ) {

        Divider()

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                Icons.Rounded.ExpandMore,
                "Close",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }

        VSpacer(12.dp)

        MaxWidthRow {
            BackButton(onTapSkipBack)
            HSpacer(20.dp)
            PlayPauseButton(state, onTapPlayPause)
            HSpacer(20.dp)
            ForwardButton(onTapSkipForward)
        }

        VSpacer(12.dp)

        PlaybackSpeedSlider(
            startingPlaybackSpeed,
            onChangePlaybackSpeed
        )

        VSpacer(16.dp)

        NarratorToggle(
            startingNarrator,
            onChangeNarrator
        )

        VSpacer(32.dp)
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
        modifier = Modifier
            .size(100.dp)
            .padding(24.dp),
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

@Composable private fun NarratorToggle(
    startingValue: AudioNarrator,
    onChangeNarrator: (narrator: AudioNarrator) -> Unit
) {
    var isChecked by remember { mutableStateOf(startingValue == ModernSblgnt) }
    
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
            onCheckedChange = { checked ->
                isChecked = checked
                onChangeNarrator(
                    if (isChecked) ModernSblgnt else ErasmianPhemister
                )
            },
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

@Composable fun PlaybackSpeedSlider(
    startingValue: Float,
    onChangePlaybackSpeed: (speed: Float) -> Unit
) {

    var playbackSpeed by remember { mutableStateOf(startingValue) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Speed",
                style = MaterialTheme.typography.caption,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${String.format("%.1f", playbackSpeed)}x",
                style = MaterialTheme.typography.caption,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        
        HSpacer(width = 4.dp)

        Slider(
            value = playbackSpeed,
            onValueChange = { newSpeed ->
                playbackSpeed = newSpeed
                onChangePlaybackSpeed(newSpeed)
            },
            valueRange = 0.5f..2.0f,
            steps = 16,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary,
                activeTickColor = MaterialTheme.colors.primary,
                inactiveTrackColor = Color.LightGray,
                inactiveTickColor = Color.LightGray
            )
        )
    }
}