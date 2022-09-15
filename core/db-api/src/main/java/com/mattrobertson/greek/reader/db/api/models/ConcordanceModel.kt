package com.mattrobertson.greek.reader.db.api.models

data class ConcordanceModel (
	val id: Int,
	val lex: String,
	val book: Int,
	val chapter: Int,
	val verse: Int
)