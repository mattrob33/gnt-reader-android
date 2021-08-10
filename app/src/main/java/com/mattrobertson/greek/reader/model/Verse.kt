package com.mattrobertson.greek.reader.model

import com.mattrobertson.greek.reader.verseref.VerseRef

/**
 * A decoded representation of a single verse.
 * @param verseRef validated verse reference (book, chapter, verse)
 * @param words ordered list of Words for the verse
 */
data class Verse (
	val verseRef: VerseRef,
	val words: List<Word>
) : Comparable<Verse> {
	/**
	 * Suitable for displaying to the user as the verse's text.
	 */
	val text: String by lazy {
		val sb = StringBuilder()
		words.forEach { word ->
			sb.append(word.text).append(" ")
		}
		sb.toString().trimEnd()
	}

	override fun compareTo(other: Verse): Int {
		return verseRef.compareTo(other.verseRef)
	}

}