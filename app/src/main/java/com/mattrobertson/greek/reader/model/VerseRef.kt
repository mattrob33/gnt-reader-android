package com.mattrobertson.greek.reader.model

import com.mattrobertson.greek.reader.util.numChaptersInBook
import com.mattrobertson.greek.reader.util.numVersesInChapter
import com.mattrobertson.greek.reader.util.verses
import java.lang.IllegalArgumentException

data class VerseRef (
	val book: Book,
	val chapter: Int,
	val verse: Int = NO_VERSE
) : Comparable<VerseRef> {

	companion object {
		const val NO_VERSE = 0

		fun fromAbsoluteChapterNum(absChapterNum: Int): VerseRef {
			var bookNum = 0

			for (bookEntry in verses.withIndex()) {
				if (absChapterNum < bookNum + bookEntry.value.size) {
					val book = Book(bookEntry.index)
					val chapter = absChapterNum - bookNum + 1

					return VerseRef(book, chapter)
				}
				else {
					bookNum += bookEntry.value.size
				}
			}

			throw IllegalArgumentException("Invalid absolute chapter num: $absChapterNum")
		}
	}

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

	override fun hashCode(): Int {
		var result = book.hashCode()
		result = 31 * result + chapter
		result = 31 * result + verse
		return result
	}
}