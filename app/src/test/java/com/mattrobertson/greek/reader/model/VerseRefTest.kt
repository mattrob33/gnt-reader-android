package com.mattrobertson.greek.reader.model

import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.Test

class VerseRefTest {

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