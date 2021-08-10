package com.mattrobertson.greek.reader.audio

import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.VerseRef

object GreekLatinAudio {

	fun getUrl(ref: VerseRef): String {
		val strChap = if (ref.chapter < 10) "0${ref.chapter}" else ref.chapter.toString()
		val bookAbbrv = getAbbrv(ref.book)
		val bookName = getTitle(ref.book)
		return "$urlBase/$bookName/$bookAbbrv${strChap}g.mp3"
	}

	private val abbrvs = arrayOf("mat","mrk","luk","joh","act","rom","1co","2co","gal","eph","phi","col","1th","2th","1ti","2ti","tit","phe","heb","jam","1pe","2pe","1jo","2jo","3jo","jud","rev")

	private val titles = arrayOf("matthew","mark","luke","john","acts","romans","1corinthians","2corinthians","galatians","ephesians","philippians","colossians","1thessalonians","2thessalonians","1timothy","2timothy","titus","philemon","hebrews","james","1peter","2peter","1john","2john","3john","jude","revelation")

	private const val urlBase = "http://www.helding.net/greeklatinaudio/greek"

	private fun getAbbrv(book: Book) = abbrvs[book.num]

	private fun getTitle(book: Book) = titles[book.num]

}