package com.mattrobertson.greek.reader.verseref

data class Word (
	val text: String,
	val lexicalForm: String,
	val parsing: WordParsing
)