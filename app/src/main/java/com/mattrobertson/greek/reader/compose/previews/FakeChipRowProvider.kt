package com.mattrobertson.greek.reader.compose.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.VerseRef

class FakeChipRowProvider: PreviewParameterProvider<List<String>> {

    override val values = sequenceOf(listOf("<100", "<50", "<30", "<20", "<15", "<10", "<5"))

}