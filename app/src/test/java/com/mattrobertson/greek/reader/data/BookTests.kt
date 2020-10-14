package com.mattrobertson.greek.reader.data

import com.mattrobertson.greek.reader.model.Book
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class BookTests {

	@Nested
	@DisplayName("Given invalid book num:")
	inner class InvalidBookNum {
		@DisplayName("exception is thrown")
		@Test
		fun `Invalid book num throws exception`() {
			assertThrows<IllegalArgumentException> {
				Book(-1)
			}

			assertThrows<IllegalArgumentException> {
				Book(27)
			}
		}
	}

	@Nested
	@DisplayName("Given valid book num:")
	inner class ValidBookNum {
		@DisplayName("exception is not thrown")
		@Test
		fun `Valid book num does not throw exception`() {
			val matthew = Book(0)
			val revelation = Book(26)
		}

		@DisplayName("book num property is correct")
		@Test
		fun `book num is correct`() {
			val matthew = Book(0)
			assertEquals(0, matthew.num)

			val revelation = Book(26)
			assertEquals(26, revelation.num)
		}

		@DisplayName("abbrv is correct")
		@Test
		fun `book abbrv is correct`() {
			val matthew = Book(0)
			assertEquals("Matt", matthew.abbrv)

			val revelation = Book(26)
			assertEquals("Rev", revelation.abbrv)
		}

		@DisplayName("title is correct")
		@Test
		fun `book title is correct`() {
			val matthew = Book(0)
			assertEquals("Matthew", matthew.title)

			val revelation = Book(26)
			assertEquals("Revelation", revelation.title)
		}
	}
}