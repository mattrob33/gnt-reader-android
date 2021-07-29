package com.mattrobertson.greek.reader.mappers

import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.model.WordParsing
import java.util.*

object VerseTextDecoder {

	fun decode(encodedText: String): List<Word> {
		val words = LinkedList<Word>()
		val encodedWordList = encodedText.split(" ")

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

		return words
	}

	fun decodeAsString(encodedText: String): String {
		val sb = StringBuilder()
		val encodedWordList = encodedText.split(" ")

		encodedWordList.forEach { encodedWord ->
			val wordParts = encodedWord.split("_")
			sb.append(wordParts[0]).append(" ")
		}

		return sb.toString().trim()
	}
}