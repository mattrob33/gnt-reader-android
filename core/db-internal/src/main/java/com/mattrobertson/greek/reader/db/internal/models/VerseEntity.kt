package com.mattrobertson.greek.reader.db.internal.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mattrobertson.greek.reader.db.internal.GntDatabase.Companion.VERSES_TABLE

@Entity(
	tableName = VERSES_TABLE,
	primaryKeys = ["book", "chapter", "verse"]
)
data class VerseEntity (
	val book: Int,
	val chapter: Int,
	val verse: Int,
	@ColumnInfo(name = "encoded_text") val encodedText: String
)