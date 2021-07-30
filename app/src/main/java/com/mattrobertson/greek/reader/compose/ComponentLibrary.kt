package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.compose.previews.FakeChipRowProvider

@Composable
fun DragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(24.dp)
                .height(4.dp),
            shape = RoundedCornerShape(2.dp),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {}
    }
}

@Composable
fun Chip(
    title: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        color = if (selected) Color(0xFF00DD00) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable {
            onSelected()
        }.border(
            width = 1.dp,
            color = Color(0xFF009900),
            shape = RoundedCornerShape(16.dp)
        )
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ScrollableChipRow(
    @PreviewParameter(FakeChipRowProvider::class)
    items: List<String>,
    onItemSelected: (index: Int) -> Unit = {}
) {
    var selectedIndex by remember { mutableStateOf(0) }

    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(items) { index, item ->
            Chip(
                title = item,
                selected = (index == selectedIndex),
                onSelected = {
                    selectedIndex = index
                    onItemSelected(index)
                }
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
