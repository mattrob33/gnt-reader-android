package com.mattrobertson.greek.reader.db.api.repo

import com.mattrobertson.greek.reader.db.internal.dao.VersesDao
import com.mattrobertson.greek.reader.db.internal.mappers.VerseMapper
import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.Verse
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.Word
import com.mattrobertson.greek.reader.verseref.mappers.VerseTextDecoder
import javax.inject.Inject

class VerseRepo @Inject constructor(private val versesDao: VersesDao) {

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

	suspend fun getVersesForChapter(ref: VerseRef) =
		getVersesForChapter(ref.book, ref.chapter)

	suspend fun getVerse(ref: VerseRef): Verse {
		val entity = versesDao.getVerse(ref.book.num, ref.chapter, ref.verse)
		return VerseMapper.fromEntity(entity)
	}

	suspend fun getVerseWords(ref: VerseRef): List<Word> {
		val verse = versesDao.getVerse(ref.book.num, ref.chapter, ref.verse)
		return VerseTextDecoder.decode(verse.encodedText)
	}

}