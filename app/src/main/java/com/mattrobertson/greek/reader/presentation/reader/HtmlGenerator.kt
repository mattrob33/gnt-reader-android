package com.mattrobertson.greek.reader.presentation.reader

import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.Verse
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.repo.VerseRepo

class HtmlGenerator(
	private val verseRepo: VerseRepo
) {

	var showVerseNumbers = true
	var showVersesNewLines = false


	suspend fun createChapterHtml(ref: VerseRef) =
		createHtmlForVerses(verseRepo.getVersesForChapter(ref))

	suspend fun createBookHtml(book: Book) =
		createHtmlForVerses(verseRepo.getVersesForBook(book))


	private fun createHtmlForVerses(verses: List<Verse>): String {
		val htmlBuilder = HtmlBuilder()

		var wordIndex = 0
		var lastVerseNum = 0
		var lastChapterNum = 0

		var prevWasEndOfSentence = false

		verses.forEach { verse ->
			val chapterNum = verse.verseRef.chapter
			val verseNum = verse.verseRef.verse

			if (chapterNum != lastChapterNum) {
				htmlBuilder.newChapter(verse.verseRef)
			}

			lastChapterNum = chapterNum

			verse.words.forEachIndexed { index, word ->

				val isUppercase = word.text.first().toUpperCase() == word.text.first()

				// Paragraph divisions
				if (isUppercase && prevWasEndOfSentence && verseNum > 1) {
					htmlBuilder.newParagraph(indent = true)
				}

				prevWasEndOfSentence = word.text.last() == '.'

				// Verse numbers
				if (showVerseNumbers) {
					if (verseNum != lastVerseNum) {
						if (showVersesNewLines) {
							if (verseNum > 1)
								htmlBuilder.endParagraph()
							htmlBuilder.newParagraph(indent = false)
						}
						if (showVerseNumbers) {
							htmlBuilder.appendVerseNum(verse.verseRef)
						}
						lastVerseNum = verseNum
					}
				}

				htmlBuilder.appendWord(word, "${verse.verseRef.book.num}_${verse.verseRef.chapter}_${verse.verseRef.verse}_$index")

				wordIndex++
			}

			if (showVersesNewLines)
				htmlBuilder.addVerseSeparator()
		}

		htmlBuilder.endParagraph()

		return htmlBuilder.toString()
	}
}