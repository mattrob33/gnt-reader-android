package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.util.AppConstants

@Composable
fun TableOfContents(
    onSelected: (index: Int) -> Unit
) {
    LazyColumn {
        itemsIndexed(AppConstants.bookTitles) { index, title ->
            Text(
                text = title,
                modifier = Modifier.clickable {
                    onSelected(index)
                }
                .padding(
                    vertical = 8.dp,
                    horizontal = 16.dp
                )
                .fillMaxWidth()
            )
            Divider()
        }
    }
}