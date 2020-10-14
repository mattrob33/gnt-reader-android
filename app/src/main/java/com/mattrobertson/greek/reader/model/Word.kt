package com.mattrobertson.greek.reader.model

data class Word (
	val text: String,
	val lexicalForm: String,
	val parsing: WordParsing
)