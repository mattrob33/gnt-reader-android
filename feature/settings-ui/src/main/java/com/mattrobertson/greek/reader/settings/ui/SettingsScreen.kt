package com.mattrobertson.greek.reader.settings.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
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
                ReaderPreview()
                VSpacer(30.dp)
                FontSizeRow()
                LineSpacingRow()
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

@Composable private fun ReaderPreview() {
    MaxWidthBox(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .border(2.dp, MaterialTheme.colors.onBackground)
    ) {
        Text(
            text = "Reader Preview",
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(80.dp)
        )
    }
}

@Composable private fun FontSizeRow() {
    SettingsRow {
        SettingsLabel(
            text = "Font size",
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable private fun LineSpacingRow() {
    SettingsRow {
        SettingsLabel(
            text = "Line spacing",
            modifier = Modifier.align(Alignment.CenterStart)
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

@Composable private fun FontChooser(
    fonts: List<FontFamily>,
    selectedFont: FontFamily,
    onSelectFont: (font: FontFamily) -> Unit,
    onDismiss: () -> Unit
) {
    MaxWidthColumn(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .heightIn(200.dp, 400.dp)
            .noRippleClickable {}
    ) {
        Divider()
        FontChooserTopBar(onDismiss = onDismiss)
        Divider()

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(fonts) { font ->
                FontItem(
                    font = font,
                    isSelected = (selectedFont.id == font.id),
                    onSelect = { onSelectFont(font) }
                )
            }
        }
    }
}

@Composable private fun FontItem(
    font: FontFamily,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
   MaxWidthRow(
       horizontalArrangement = Arrangement.Start,
       modifier = Modifier
           .clickable { onSelect() }
           .padding(vertical = 8.dp)
   ) {

       HSpacer(20.dp)

       if (isSelected) {
           Icon(
               imageVector = Icons.Rounded.Check,
               contentDescription = null,
               tint = MaterialTheme.colors.onBackground,
               modifier = Modifier.size(20.dp)
           )
       }
       else {
           HSpacer(20.dp)
       }

       HSpacer(20.dp)

       Text(
           text = font.displayName,
           fontFamily = DefaultMaterialFont,
           fontSize = 20.sp,
           color = MaterialTheme.colors.onBackground
       )
   }
}

@Composable private fun FontChooserTopBar(
    onDismiss: () -> Unit
) {
    MaxWidthBox(
        modifier = Modifier
            .height(40.dp)
            .noRippleClickable {}
    ) {
        Text(
            text = "Font",
            style = MaterialTheme.typography.h1,
            fontSize = 24.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(40.dp)
                .width(60.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close font chooser",
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}