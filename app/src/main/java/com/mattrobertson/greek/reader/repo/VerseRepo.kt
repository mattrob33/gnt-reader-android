package com.mattrobertson.greek.reader.repo

import com.mattrobertson.greek.reader.data.dao.VersesDao
import com.mattrobertson.greek.reader.mappers.VerseMapper
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.Verse
import com.mattrobertson.greek.reader.model.VerseRef

class VerseRepo(private val versesDao: VersesDao) {

	suspend fun getVersesForBook(book: Book): List<Verse> {
		return versesDao.getVersesForBook(book.num).map { entity ->
			VerseMapper.fromEntity(entity)
		}
	}

	suspend fun getVersesForChapter(book: Book, chapter: Int): List<Verse> {
		return versesDao.getVersesForChapter(book.num, chapter).map { entity ->
			VerseMapper.fromEntity(entity)
		}
	}

	suspend fun getVersesForChapter(ref: VerseRef) = getVersesForChapter(ref.book, ref.chapter)

	suspend fun getVerse(ref: VerseRef) = versesDao.getVerse(ref.book.num, ref.chapter, ref.verse)

}