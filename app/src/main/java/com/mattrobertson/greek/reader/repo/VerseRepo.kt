package com.mattrobertson.greek.reader.repo

import com.mattrobertson.greek.reader.data.VersesDao
import com.mattrobertson.greek.reader.mappers.VerseMapper
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.Verse

class VerseRepo(private val versesDao: VersesDao) {

	suspend fun getVersesForChapter(book: Book, chapter: Int): List<Verse> {
		return versesDao.getVersesForChapter(book.num, chapter).map { entity ->
			VerseMapper.fromEntity(entity)
		}
	}

}