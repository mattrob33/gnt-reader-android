package com.mattrobertson.greek.reader.vocab.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef

class FakeVerseRefProvider: PreviewParameterProvider<VerseRef> {

    override val values = sequenceOf(
        VerseRef(Book.MATTHEW, 1)
    )

}