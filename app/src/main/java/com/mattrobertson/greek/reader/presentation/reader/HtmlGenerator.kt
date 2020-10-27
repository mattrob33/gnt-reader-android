package com.mattrobertson.greek.reader.presentation.reader

import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.objects.HtmlBuilder
import com.mattrobertson.greek.reader.repo.VerseRepo

class HtmlGenerator(
	private val verseRepo: VerseRepo,
	var showVerseNumbers: Boolean = true,
	var showVersesNewLines: Boolean = false
) {

	suspend fun createBookHtml(
			book: Book,
			showVerseNumbers: Boolean = true,
			showVersesNewLines: Boolean = false
	): String {
		val verses = verseRepo.getVersesForBook(book)

		val htmlBuilder = HtmlBuilder()

		var wordIndex = 0
		var lastVerseNum = 0
		var lastChapterNum = 0

		var prevWasEndOfSentence = false

		verses.forEach { verse ->
			val chapterNum = verse.verseRef.chapter
			val verseNum = verse.verseRef.verse

			if (chapterNum != lastChapterNum) {
				if (chapterNum > 1)
					htmlBuilder.endChapter()

				htmlBuilder.newChapter(verse.verseRef)
			}

			lastChapterNum = chapterNum

			verse.words.forEach { word ->

				val isUppercase = word.text.first().toUpperCase() == word.text.first()

				// Paragraph divisions
				if (isUppercase && prevWasEndOfSentence && verseNum > 1) {
					htmlBuilder.newParagraph(indent = true)
				}

				prevWasEndOfSentence = word.text.last() == '.'

				// Verse numbers
				if (verseNum != lastVerseNum) {
					if (showVersesNewLines) {
						if (verseNum > 1)
							htmlBuilder.endParagraph()
						htmlBuilder.newParagraph(indent = false)
					}
					if (showVerseNumbers) {
						htmlBuilder.appendVerseNum(verseNum)
					}
					lastVerseNum = verseNum
				}

				htmlBuilder.appendWord(word, wordIndex)

				wordIndex++
			}
		}

		htmlBuilder.endParagraph()

		return htmlBuilder.toString()
	}
	
}