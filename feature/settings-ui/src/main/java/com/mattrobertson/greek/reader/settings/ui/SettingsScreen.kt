package com.mattrobertson.greek.reader.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mattrobertson.greek.reader.ui.lib.*
import com.mattrobertson.greek.reader.ui.settings.FontFamily
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import androidx.compose.ui.text.font.FontFamily.Companion.Default as DefaultMaterialFont

@Preview(showBackground = true)
@Composable private fun SettingsScreen_Preview() {
    AppTheme {
        SettingsScreen(
            onBack = {}
        )
    }
}

@Composable fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    var fontChooserVisible by remember { mutableStateOf(false) }

    MaxSizeBox {

        MaxSizeColumn(verticalArrangement = Arrangement.Top) {

            TopBar(onBack = onBack)
            Divider()
            VSpacer(40.dp)

            MaxWidthColumn {
                ReaderPreview(settings)

                VSpacer(30.dp)

                FontSizeRow(
                    fontSize = settings.fontSize,
                    onChange = { size ->
                        viewModel.setFontSize(size)
                    }
                )

                VSpacer(16.dp)

                LineSpacingRow(
                    lineSpacing = settings.lineSpacing,
                    onChange = { lineSpacing ->
                        viewModel.setLineSpacingMultiplier(lineSpacing)
                    }
                )

                FontFamilyRow(
                    font = settings.font,
                    onClick = {
                        fontChooserVisible = !fontChooserVisible
                    }
                )

                ShowVerseNumbersRow(
                    isEnabled = settings.showVerseNumbers,
                    onChange = viewModel::setShowVerseNumbers
                )

                VersesOnNewLinesRow(
                    isEnabled = settings.versesOnNewLines,
                    onChange = viewModel::setVersesOnNewLines
                )
            }
        }

        AnimatedVisibility(
            visible = fontChooserVisible,
            enter = slideInVertically(initialOffsetY = { height -> height / 4 }),
            exit = slideOutVertically(targetOffsetY = { height -> height }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FontChooser(
                fonts = FontFamily.allFonts,
                selectedFont = settings.font,
                onSelectFont = viewModel::setFont,
                onDismiss = { fontChooserVisible = false }
            )
        }
    }
}

@Composable private fun TopBar(onBack: () -> Unit) {
    MaxWidthBox(
        modifier = Modifier.height(60.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colors.onBackground
            )
        }

        Text(
            text = "Settings",
            style = MaterialTheme.typography.h1,
            fontSize = 24.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable private fun FontSizeRow(
    fontSize: TextUnit,
    onChange: (size: TextUnit) -> Unit
) {
    var sliderValue by remember { mutableStateOf(fontSize.value) }

    SettingsColumnRow {
        SettingsLabel(
            text = "Font size",
            modifier = Modifier.align(Alignment.Start)
        )

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onChange(it.sp)
            },
            valueRange = 14f..30f,
            steps = 9
        )
    }
}

@Composable private fun LineSpacingRow(
    lineSpacing: Float,
    onChange: (spacingMultiplier: Float) -> Unit
) {
    var sliderValue by remember { mutableStateOf(lineSpacing) }

    SettingsColumnRow {
        SettingsLabel(
            text = "Line spacing",
            modifier = Modifier.align(Alignment.Start)
        )

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onChange(it)
            },
            valueRange = 1.0f..2.0f,
            steps = 6
        )
    }
}

@Composable private fun FontFamilyRow(
    font: FontFamily,
    onClick: () -> Unit
) {
    SettingsRow(
        onClick = onClick
    ) {
        SettingsLabel(
            text = "Font",
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = font.displayName,
            fontFamily = DefaultMaterialFont,
            fontSize = 18.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable private fun ShowVerseNumbersRow(
    isEnabled: Boolean,
    onChange: (isEnabled: Boolean) -> Unit
) {

    var isChecked by remember { mutableStateOf(isEnabled) }

    SettingsRow(
        onClick = {}
    ) {
        SettingsLabel(
            text = "Show verse numbers",
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onChange(it)
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable private fun VersesOnNewLinesRow(
    isEnabled: Boolean,
    onChange: (isEnabled: Boolean) -> Unit
) {

    var isChecked by remember { mutableStateOf(isEnabled) }

    SettingsRow {
        SettingsLabel(
            text = "Verses on separate lines",
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onChange(it)
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable private fun SettingsColumnRow(
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    MaxWidthColumn(
        content = content,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 24.dp)
    )
}

@Composable private fun SettingsRow(
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    MaxWidthBox(
        content = content,
        modifier = Modifier
            .clickable { onClick() }
            .height(60.dp)
            .padding(horizontal = 24.dp)
    )
}

@Composable private fun SettingsLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontFamily = DefaultMaterialFont,
        fontSize = 18.sp,
        fontWeight = Bold,
        color = MaterialTheme.colors.onBackground,
        modifier = modifier
    )
}

