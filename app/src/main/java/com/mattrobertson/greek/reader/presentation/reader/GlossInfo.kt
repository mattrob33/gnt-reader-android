package com.mattrobertson.greek.reader.presentation.reader

data class GlossInfo (
        val lex: String,
        val gloss: String,
        val parsing: String,
        val frequency: Int
)