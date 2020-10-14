package com.mattrobertson.greek.reader.mappers

import com.mattrobertson.greek.reader.model.EncodedVerse
import com.mattrobertson.greek.reader.model.Verse
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.model.WordParsing
import java.util.*

object VerseDecoder {
	fun decode(encodedVerse: EncodedVerse): Verse {
		val words = LinkedList<Word>()

		val encodedWordList = encodedVerse.encodedText.split(" ")

		encodedWordList.forEach { encodedWord ->
			val wordParts = encodedWord.split("_")
			words.add(
				Word(
					text = wordParts[0],
					lexicalForm = wordParts[1],
					parsing = WordParsing.decode(wordParts[2])
				)
			)
		}

		return Verse(encodedVerse.verseRef, words)
	}
}