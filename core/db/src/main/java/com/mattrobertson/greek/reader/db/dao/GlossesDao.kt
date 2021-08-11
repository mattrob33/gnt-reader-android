package com.mattrobertson.greek.reader.db.dao

import androidx.room.*
import com.mattrobertson.greek.reader.db.GntDatabase.Companion.GLOSSES_TABLE
import com.mattrobertson.greek.reader.db.models.GlossEntity

@Dao
interface GlossesDao {

	@Insert
	suspend fun insert(gloss: GlossEntity)

	@Update
	suspend fun update(gloss: GlossEntity)

	@Delete
	suspend fun delete(vararg gloss: GlossEntity)

	@Query("SELECT * FROM $GLOSSES_TABLE WHERE `lex` = :lex LIMIT 1")
	suspend fun getGlossEntity(lex: String): GlossEntity

	@Query("SELECT `gloss` FROM $GLOSSES_TABLE WHERE `lex` = :lex LIMIT 1")
	suspend fun getGloss(lex: String): String
}