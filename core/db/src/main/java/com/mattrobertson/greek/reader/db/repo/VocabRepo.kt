package com.mattrobertson.greek.reader.db.repo

import com.mattrobertson.greek.reader.db.dao.VocabDao
import com.mattrobertson.greek.reader.verseref.VerseRef

class VocabRepo(private val vocabDao: VocabDao) {

	suspend fun getVocabWordsForChapter(ref: VerseRef, maxOcc: Int) =
		vocabDao.getVocabWordsForChapter(ref, maxOcc)

}