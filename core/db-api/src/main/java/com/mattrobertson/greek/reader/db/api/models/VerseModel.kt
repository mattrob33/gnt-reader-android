package com.mattrobertson.greek.reader.db.api.models

data class VerseModel (
	val book: Int,
	val chapter: Int,
	val verse: Int,
	val encodedText: String
)