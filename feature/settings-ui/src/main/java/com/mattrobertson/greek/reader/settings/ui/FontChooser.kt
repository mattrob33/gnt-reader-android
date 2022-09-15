package com.mattrobertson.greek.reader.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.ui.lib.*
import com.mattrobertson.greek.reader.ui.settings.FontFamily

@Composable internal fun FontChooser(
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
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
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