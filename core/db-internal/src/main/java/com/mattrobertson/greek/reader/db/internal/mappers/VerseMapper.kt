package com.mattrobertson.greek.reader.db.internal.mappers

import com.mattrobertson.greek.reader.db.internal.models.VerseEntity
import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.Verse
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.mappers.VerseTextDecoder

object VerseMapper {
	fun fromEntity(entity: VerseEntity): Verse {
		return Verse(
			verseRef = VerseRef(Book(entity.book), entity.chapter, entity.verse),
			words = VerseTextDecoder.decode(entity.encodedText)
		)
	}
}