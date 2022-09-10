package com.mattrobertson.greek.reader.ui.lib

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable fun SelectedChip_Preview() {
    AppTheme {
        Chip(
            title = "50x",
            selected = true,
            onSelected = {},
            outlineColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.primary,
            textColor = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable fun UnselectedChip_Preview() {
    AppTheme {
        Chip(
            title = "50x",
            selected = false,
            onSelected = {},
            outlineColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.primary,
            textColor = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun Chip(
    title: String,
    selected: Boolean,
    onSelected: () -> Unit,
    outlineColor: Color,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickable {
                onSelected()
            }
            .defaultMinSize(
                minHeight = 32.dp,
                minWidth = 60.dp
            )
            .border(
                width = 1.dp,
                color = outlineColor,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = if (selected) backgroundColor else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            color = textColor,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ScrollableChipRow(
    items: List<String>,
    onItemSelected: (index: Int) -> Unit = {},
    outlineColor: Color,
    backgroundColor: Color,
    textColor: Color
) {
    var selectedIndex by remember { mutableStateOf(0) }

    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            HSpacer(16.dp)
        }
        itemsIndexed(items) { index, item ->
            val selected = (index == selectedIndex)
            Chip(
                title = item,
                selected = selected,
                onSelected = {
                    selectedIndex = index
                    onItemSelected(index)
                },
                outlineColor = outlineColor,
                backgroundColor = backgroundColor,
                textColor = if (selected) MaterialTheme.colors.background else textColor
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
