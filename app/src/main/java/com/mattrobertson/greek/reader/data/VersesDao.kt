package com.mattrobertson.greek.reader.data

import androidx.room.*
import com.mattrobertson.greek.reader.data.VerseDatabase.Companion.VERSES_TABLE
import com.mattrobertson.greek.reader.data.models.VerseEntity
import com.mattrobertson.greek.reader.model.Book

@Dao
interface VersesDao {

	@Insert
	suspend fun insert(verse: VerseEntity)

	@Update
	suspend fun update(verse: VerseEntity)

	@Delete
	suspend fun delete(vararg verse: VerseEntity)

	@Query("SELECT * FROM $VERSES_TABLE WHERE `book` = :book AND `chapter` = :chapter AND `verse` = :verse LIMIT 1")
	suspend fun getVerse(book: Int, chapter: Int, verse: Int): VerseEntity

	@Query("SELECT * FROM $VERSES_TABLE WHERE `book` = :book AND `chapter` = :chapter ORDER BY `verse` ASC")
	suspend fun getVersesForChapter(book: Int, chapter: Int): List<VerseEntity>

	@Query("SELECT * FROM $VERSES_TABLE WHERE `book` = :book ORDER BY `chapter`,`verse` ASC")
	suspend fun getVersesForBook(book: Int): List<VerseEntity>
}