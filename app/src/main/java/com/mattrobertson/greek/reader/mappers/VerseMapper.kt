package com.mattrobertson.greek.reader.mappers

import com.mattrobertson.greek.reader.data.models.VerseEntity
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.Verse
import com.mattrobertson.greek.reader.model.VerseRef

object VerseMapper {
	fun fromEntity(entity: VerseEntity): Verse {
		return Verse(
			verseRef = VerseRef(Book(entity.book), entity.chapter, entity.verse),
			words = VerseTextDecoder.decode(entity.encodedText)
		)
	}
}