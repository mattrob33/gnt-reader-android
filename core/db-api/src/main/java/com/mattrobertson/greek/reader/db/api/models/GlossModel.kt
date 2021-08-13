package com.mattrobertson.greek.reader.db.api.models

data class GlossModel (
	val lex: String,
	val gloss: String,
	val occ: Int
)