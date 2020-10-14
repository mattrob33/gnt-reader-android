package com.mattrobertson.greek.reader.model

/**
 * An encoded representation of a single verse.
 * @param verseRef contains a validated verse reference (book, chapter, verse)
 * @param encodedText contains the encoded text of a verse and must be decoded for use
 */
data class EncodedVerse (
	val verseRef: VerseRef,
	val encodedText: String
) : Comparable<EncodedVerse> {

	override fun compareTo(other: EncodedVerse): Int {
		return verseRef.compareTo(other.verseRef)
	}

}