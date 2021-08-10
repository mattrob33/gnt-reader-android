package com.mattrobertson.greek.reader.data

import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VerseRefTests {

	@Nested
	@DisplayName("Given invalid reference:")
	inner class InvalidRef {

		@Test
		@DisplayName("exception is thrown")
		fun `invalid reference throws exception`() {
			assertThrows<IllegalArgumentException> {
				VerseRef(Book.MATTHEW, 0, 1)
			}

			assertThrows<IllegalArgumentException> {
				VerseRef(Book.MATTHEW, 1, 0)
			}

			assertThrows<IllegalArgumentException> {
				VerseRef(Book.MATTHEW, 28, 21)
			}

			assertThrows<IllegalArgumentException> {
				VerseRef(Book.MATTHEW, 29, 1)
			}

			assertThrows<IllegalArgumentException> {
				VerseRef(Book(27), 1, 1)
			}
		}
	}

	@Nested
	@DisplayName("Given valid reference:")
	inner class ValidRef {

		@Test
		@DisplayName("exception is not thrown")
		fun `valid reference does not throw exception`() {
			VerseRef(Book.MATTHEW, 1, 1)
			VerseRef(Book.MATTHEW, 28, 20)
		}

		@Test
		@DisplayName("compareTo is correct")
		fun `compareTo works correctly`() {
			assert(
				VerseRef(Book.MARK, 1, 1) > VerseRef(Book.MATTHEW, 1, 1)
			)
			assert(
				VerseRef(Book.MATTHEW, 2, 1) > VerseRef(Book.MATTHEW, 1, 1)
			)
			assert(
				VerseRef(Book.MATTHEW, 1, 2) > VerseRef(Book.MATTHEW, 1, 1)
			)

			assert(
				VerseRef(Book.MATTHEW, 1, 1) < VerseRef(Book.MARK, 1, 1)
			)
			assert(
				VerseRef(Book.MATTHEW, 1, 1) < VerseRef(Book.MATTHEW, 2, 1)
			)
			assert(
				VerseRef(Book.MATTHEW, 1, 1) < VerseRef(Book.MATTHEW, 1, 2)
			)
		}
	}

}