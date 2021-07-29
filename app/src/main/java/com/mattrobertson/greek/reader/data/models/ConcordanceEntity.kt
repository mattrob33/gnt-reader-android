package com.mattrobertson.greek.reader.data.models

import androidx.room.Entity
import com.mattrobertson.greek.reader.data.VerseDatabase.Companion.CONCORDANCE_TABLE

@Entity(
	tableName = CONCORDANCE_TABLE,
	primaryKeys = ["id"]
)
data class ConcordanceEntity (
	val id: Int,
	val lex: String,
	val book: Int,
	val chapter: Int,
	val verse: Int
)