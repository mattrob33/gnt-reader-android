package com.mattrobertson.greek.reader.compose.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.VerseRef

class FakeVerseRefProvider: PreviewParameterProvider<VerseRef> {

    override val values = sequenceOf(
        VerseRef(Book.MATTHEW, 1)
    )

}