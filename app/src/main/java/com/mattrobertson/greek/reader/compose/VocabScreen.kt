package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.mattrobertson.greek.reader.compose.previews.FakeVerseRefProvider
import com.mattrobertson.greek.reader.model.VerseRef

@Composable
@Preview(name = "Vocab")
fun VocabScreen(
    @PreviewParameter(FakeVerseRefProvider::class)
    ref: VerseRef
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val chips = listOf("100+", "50-99", "26-49", "21-25", "16-20", "11-15", "<10")
        ScrollableChipRow(chips)
    }
}