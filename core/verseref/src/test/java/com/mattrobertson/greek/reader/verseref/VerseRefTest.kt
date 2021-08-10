package com.mattrobertson.greek.reader.verseref

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VerseRefTest {

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

	@Test
	fun `parse from abs chapter is correct`() {
		var ref = VerseRef.fromAbsoluteChapterNum(0)
		assertEquals(VerseRef(Book.MATTHEW, 1), ref)

		ref = VerseRef.fromAbsoluteChapterNum(1)
		assertEquals(VerseRef(Book.MATTHEW, 2), ref)

		ref = VerseRef.fromAbsoluteChapterNum(28)
		assertEquals(VerseRef(Book.MARK, 1), ref)

		ref = VerseRef.fromAbsoluteChapterNum(259)
		assertEquals(VerseRef(Book.REVELATION, 22), ref)
	}

}