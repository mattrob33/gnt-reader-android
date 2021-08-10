package com.mattrobertson.greek.reader.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.mattrobertson.greek.reader.db.VerseDatabase.Companion.CONCORDANCE_TABLE
import com.mattrobertson.greek.reader.db.VerseDatabase.Companion.GLOSSES_TABLE
import com.mattrobertson.greek.reader.db.models.GlossEntity
import com.mattrobertson.greek.reader.verseref.VerseRef

@Dao
interface VocabDao {

	@Query("SELECT * FROM $GLOSSES_TABLE WHERE `occ` <= :maxOcc ORDER BY `occ` DESC")
	suspend fun getVocabWords(maxOcc: Int): List<GlossEntity>

	@Query(
		"SELECT * FROM $GLOSSES_TABLE " +
				"INNER JOIN $CONCORDANCE_TABLE ON ${GLOSSES_TABLE}.lex = ${CONCORDANCE_TABLE}.lex " +
				"WHERE ${GLOSSES_TABLE}.lex IN ( " +
					"SELECT lex FROM $CONCORDANCE_TABLE WHERE book = :book AND chapter = :chapter" +
				" ) " +
				"AND occ <= :maxOcc " +
				"GROUP BY ${GLOSSES_TABLE}.lex " +
				"ORDER BY occ DESC, ${GLOSSES_TABLE}.lex ASC"
	)
	suspend fun getVocabWordsForChapter(maxOcc: Int, book: Int, chapter: Int): List<GlossEntity>

	suspend fun getVocabWordsForChapter(ref: VerseRef, maxOcc: Int) =
		getVocabWordsForChapter(maxOcc, ref.book.num, ref.chapter)
}