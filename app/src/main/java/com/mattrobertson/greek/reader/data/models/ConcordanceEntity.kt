package com.mattrobertson.greek.reader.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mattrobertson.greek.reader.data.VerseDatabase.Companion.CONCORDANCE_TABLE

@Entity(
	tableName = CONCORDANCE_TABLE,
	indices = [
		Index(
			name = "concordance_lex",
			unique = false,
			value = ["lex", "book", "chapter", "verse"]
		)
	]
)
data class ConcordanceEntity (
	@PrimaryKey val id: Int,
	val lex: String,
	val book: Int,
	val chapter: Int,
	val verse: Int
)