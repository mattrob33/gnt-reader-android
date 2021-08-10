package com.mattrobertson.greek.reader.gloss.model

data class GlossInfo (
        val lex: String,
        val gloss: String,
        val parsing: String,
        val frequency: Int
)