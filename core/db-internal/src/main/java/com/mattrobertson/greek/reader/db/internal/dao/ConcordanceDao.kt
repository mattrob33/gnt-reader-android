package com.mattrobertson.greek.reader.db.internal.dao

import androidx.room.*
import com.mattrobertson.greek.reader.db.internal.GntDatabase.Companion.CONCORDANCE_TABLE
import com.mattrobertson.greek.reader.db.internal.models.ConcordanceEntity

@Dao
interface ConcordanceDao {

	@Insert
	suspend fun insert(concordance: ConcordanceEntity)

	@Update
	suspend fun update(concordance: ConcordanceEntity)

	@Delete
	suspend fun delete(vararg concordance: ConcordanceEntity)

	@Query("SELECT * FROM $CONCORDANCE_TABLE WHERE `lex` = :lex ORDER BY `book`,`chapter`,`verse` ASC")
	suspend fun getConcordanceEntries(lex: String): List<ConcordanceEntity>

	@Query("SELECT * FROM $CONCORDANCE_TABLE WHERE `lex` = :lex ORDER BY `book`,`chapter`,`verse` ASC LIMIT :limit")
	suspend fun getConcordanceEntries(lex: String, limit: Int): List<ConcordanceEntity>
}