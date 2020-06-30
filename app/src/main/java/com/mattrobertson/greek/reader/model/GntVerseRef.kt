package com.mattrobertson.greek.reader.model

import com.mattrobertson.greek.reader.util.AppConstants.NO_VERSE

data class GntVerseRef (
        val book: Int,
        val chapter: Int,
        val verse: Int = NO_VERSE
)