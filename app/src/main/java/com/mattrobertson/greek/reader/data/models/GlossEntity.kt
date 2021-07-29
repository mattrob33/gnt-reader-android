package com.mattrobertson.greek.reader.data.models

import androidx.room.Entity
import com.mattrobertson.greek.reader.data.VerseDatabase.Companion.GLOSSES_TABLE

@Entity(
	tableName = GLOSSES_TABLE
)
data class GlossEntity (
	val lex: String,
	val gloss: String,
	val occ: Int
)