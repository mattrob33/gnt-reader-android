package com.mattrobertson.greek.reader.model

import com.mattrobertson.greek.reader.util.numChaptersInBook
import com.mattrobertson.greek.reader.util.numVersesInChapter

data class VerseRef (
	val book: Book,
	val chapter: Int,
	val verse: Int
) {
	/**
	 * Validates inputs. Note that Book is self-validating and is thus not validated here.
 	 */
	init {
		require(chapter >= 1) {
			"Verse reference chapter cannot be less than 1"
		}

		require(chapter <= numChaptersInBook(book)) {
			"${book.title} does not have $chapter chapters"
		}

		require(verse >= 1) {
			"Verse reference number cannot be less than 1"
		}

		require(verse <= numVersesInChapter(book, chapter)) {
			"${book.title} $chapter does not have $verse verses"
		}
	}
}