package com.mattrobertson.greek.reader.html

import com.mattrobertson.greek.reader.model.Verse
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.repo.VerseRepo
import com.mattrobertson.greek.reader.viewsettings.ViewSettings

class HtmlGenerator(
	private val verseRepo: VerseRepo
) {

	companion object {
		private val cache: HashMap<ViewSettings, HashMap<VerseRef, String>> = hashMapOf()
	}

	val viewSettings = ViewSettings()

	suspend fun getChapterHtml(ref: VerseRef): String {
		val cachedHtml = cache[viewSettings]?.get(ref)
		cachedHtml?.let { return it }

		val verses = verseRepo.getVersesForChapter(ref)
		val html = createHtmlForVerses(verses)

		if (cache[viewSettings] == null)
			cache[viewSettings] = hashMapOf()

		cache[viewSettings]!![ref] = html

		return html
	}

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

				val isUppercase = word.text.first().isUpperCase()

				// Paragraph divisions
				if (isUppercase && prevWasEndOfSentence && verseNum > 1) {
					htmlBuilder.newParagraph(indent = true)
				}

				prevWasEndOfSentence = word.text.last() == '.'

				// Verse numbers
				if (viewSettings.showVerseNumbers) {
					if (verseNum != lastVerseNum) {
						if (viewSettings.showVersesNewLines) {
							if (verseNum > 1)
								htmlBuilder.endParagraph()
							htmlBuilder.newParagraph(indent = false)
						}
						if (viewSettings.showVerseNumbers) {
							htmlBuilder.appendVerseNum(verse.verseRef)
						}
						lastVerseNum = verseNum
					}
				}

				htmlBuilder.appendWord(word, "${verse.verseRef.book.num}_${verse.verseRef.chapter}_${verse.verseRef.verse}_$index")

				wordIndex++
			}

			if (viewSettings.showVersesNewLines)
				htmlBuilder.addVerseSeparator()
		}

		htmlBuilder.endParagraph()

		return htmlBuilder.toString()
	}
}