package com.mattrobertson.greek.reader.model

import com.mattrobertson.greek.reader.util.numChaptersInBook
import com.mattrobertson.greek.reader.util.numVersesInChapter

data class VerseRef (
	val book: Book,
	val chapter: Int,
	val verse: Int
) : Comparable<VerseRef> {
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

	override fun equals(other: Any?): Boolean {
		if (other !is VerseRef) return false
		return (book == other.book &&
			chapter == other.chapter &&
			verse == other.verse)
	}

	override fun compareTo(other: VerseRef): Int {
		return when {
			book == other.book -> {
				when {
					chapter == other.chapter -> {
						when {
							verse == other.verse -> 0
							verse < other.verse -> -1
							else -> 1
						}
					}
					chapter < other.chapter -> -1
					else -> 1
				}
			}
			book < other.book -> -1
			else -> 1
		}
	}
}