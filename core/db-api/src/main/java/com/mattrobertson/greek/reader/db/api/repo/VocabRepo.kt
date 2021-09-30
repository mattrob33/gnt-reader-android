package com.mattrobertson.greek.reader.db.api.repo

import com.mattrobertson.greek.reader.db.api.mappers.mapGlossEntity
import com.mattrobertson.greek.reader.db.api.models.GlossModel
import com.mattrobertson.greek.reader.db.internal.dao.VocabDao
import com.mattrobertson.greek.reader.verseref.VerseRef

class VocabRepo(private val vocabDao: VocabDao) {

	suspend fun getVocabWordsForChapter(ref: VerseRef, maxOcc: Int): List< GlossModel> {
		return vocabDao.getVocabWordsForChapter(ref, maxOcc).map {
			mapGlossEntity(it)
		}
	}

}