package com.mattrobertson.greek.reader.model

class Book private constructor(
	val num: Int
) {
	val abbrv = abbrvs[num]
	val title = titles[num]

	companion object {
		private val VALID_BOOKS = 0..26

		private val abbrvs = arrayOf("Matt", "Mark", "Luke", "John", "Acts", "Rom", "1 Cor", "2 Cor", "Gal", "Eph", "Phil", "Col", "1 Thess", "2 Thess", "1 Tim", "2 Tim", "Titus", "Phlm", "Heb", "James", "1 Pet", "2 Pet", "1 John", "2 John", "3 John", "Jude", "Rev")
		private val titles = arrayOf("Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1 Corinthians", "2 Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians", "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus", "Philemon", "Hebrews", "James", "1 Peter", "2 Peter", "1 John", "2 John", "3 John", "Jude", "Revelation")

		fun fromBookNum(num: Int): Book {
			if (num !in VALID_BOOKS) throw IllegalArgumentException("Invalid book number $num")

			return Book(num)
		}
	}
}