package com.mattrobertson.greek.reader.db.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mattrobertson.greek.reader.db.GntDatabase.Companion.GLOSSES_TABLE

@Entity(
	tableName = GLOSSES_TABLE,
	indices = [
		Index(
			name = "gloss_lex",
			unique = false,
			value = ["lex", "gloss", "occ"]
		)
	]
)
data class GlossEntity (
	@PrimaryKey val lex: String,
	val gloss: String,
	val occ: Int
)