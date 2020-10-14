package com.mattrobertson.greek.reader.model

data class Book(val num: Int): Comparable<Book> {

	init {
		require(num in VALID_BOOKS) {
			"Invalid book number $num"
		}
	}

	val abbrv = abbrvs[num]
	val title = titles[num]

	companion object {
		private val VALID_BOOKS = 0..26

		private val abbrvs = arrayOf("Matt", "Mark", "Luke", "John", "Acts", "Rom", "1 Cor", "2 Cor", "Gal", "Eph", "Phil", "Col", "1 Thess", "2 Thess", "1 Tim", "2 Tim", "Titus", "Phlm", "Heb", "James", "1 Pet", "2 Pet", "1 John", "2 John", "3 John", "Jude", "Rev")
		private val titles = arrayOf("Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1 Corinthians", "2 Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians", "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus", "Philemon", "Hebrews", "James", "1 Peter", "2 Peter", "1 John", "2 John", "3 John", "Jude", "Revelation")

		val MATTHEW = Book(0)
		val MARK = Book(1)
		val LUKE = Book(2)
		val JOHN = Book(3)
		val ACTS = Book(4)
		val ROMANS = Book(5)
		val FIRST_CORINTHIANS = Book(6)
		val SECOND_CORINTHIANS = Book(7)
		val GALATIANS = Book(8)
		val EPHESIANS = Book(9)
		val PHILIPPIANS = Book(10)
		val COLOSSIANS = Book(11)
		val FIRST_THESSALONIANS = Book(12)
		val SECOND_THESSALONIANS = Book(13)
		val FIRST_TIMOTHY = Book(14)
		val SECOND_TIMOTHY = Book(15)
		val TITUS = Book(16)
		val PHILEMON = Book(17)
		val HEBREWS = Book(18)
		val JAMES = Book(19)
		val FIRST_PETER = Book(20)
		val SECOND_PETER = Book(21)
		val FIRST_JOHN = Book(22)
		val SECOND_JOHN = Book(23)
		val THIRD_JOHN = Book(24)
		val JUDE = Book(25)
		val REVELATION = Book(26)
	}

	override fun equals(other: Any?): Boolean {
		if (other !is Book) return false

		return num == other.num
	}

	override fun compareTo(other: Book): Int {
		return num.compareTo(other.num)
	}
}