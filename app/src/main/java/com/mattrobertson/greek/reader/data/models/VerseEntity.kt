package com.mattrobertson.greek.reader.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mattrobertson.greek.reader.data.VerseDatabase.Companion.VERSES_TABLE

@Entity(tableName = VERSES_TABLE)
data class VerseEntity (
	val book: Int,
	val chapter: Int,
	val verse: Int,
	@ColumnInfo(name = "encoded_text") val encodedText: String
)