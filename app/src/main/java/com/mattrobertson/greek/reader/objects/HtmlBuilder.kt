package com.mattrobertson.greek.reader.objects

import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.model.Word
import java.lang.StringBuilder

class HtmlBuilder {
	private val sb = StringBuilder()

	fun newParagraph(indent: Boolean): HtmlBuilder {
		if (indent)
			sb.append("<p style=\"text-indent: 1em;\">")
		else
			sb.append("<p>")
		return this
	}

	fun endParagraph(): HtmlBuilder {
		sb.append("</p>")
		return this
	}

	fun newChapter(verseRef: VerseRef): HtmlBuilder {
		sb.append("<p style=\"text-indent: 4px;\">")
			.append("<span style=\"font-size: xx-large; font-weight: bold; outline-style: double; outline-width: 2px;\"><a name=\"${verseRef.book.num}_${verseRef.chapter}_${verseRef.verse}\">&nbsp;${verseRef.chapter}&nbsp;</a></span>&nbsp;&nbsp;")
		return this
	}

	fun endChapter(): HtmlBuilder {
		sb.append("</p><br>")
		return this
	}

	fun appendVerseNum(verseNum: Int): HtmlBuilder {
		sb.append("<sup style=\"font-size:0.65em;\">$verseNum</sup>")
		return this
	}

	fun appendWord(word: Word, wordIndex: Int): HtmlBuilder {
		sb.append("<span id=\"$wordIndex\" onClick=\"onWordClick(this.id, '${word.text}', '${word.lexicalForm}', '${word.parsing.codedParsing}');\">${word.text}</span> ")
		return this
	}

	override fun toString() = sb.toString()
}