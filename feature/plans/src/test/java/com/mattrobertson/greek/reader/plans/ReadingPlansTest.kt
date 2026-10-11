package com.mattrobertson.greek.reader.plans

import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingPlansTest {
    @Test fun restoresAllLegacyPlansAndDayCounts() {
        assertEquals(6, ReadingPlans.all.size)
        assertEquals(listOf(260, 28, 89, 31, 15, 21), ReadingPlans.all.map { it.days.size })
    }

    @Test fun wallaceYearUsesRevolvingDoor() {
        val days = ReadingPlans.all.first().days
        assertEquals(listOf(VerseRef(Book.JOHN, 1)), days[0])
        assertEquals(listOf(VerseRef(Book.JOHN, 1), VerseRef(Book.JOHN, 2)), days[1])
        assertEquals(listOf(VerseRef(Book.JOHN, 2), VerseRef(Book.JOHN, 3)), days[2])
        assertEquals(listOf(VerseRef(Book.HEBREWS, 12), VerseRef(Book.HEBREWS, 13)), days.last())
        assertTrue(days.drop(1).all { it.size == 2 })
    }

    @Test fun everyReferenceIsAValidNewTestamentChapter() {
        ReadingPlans.all.flatMap { it.days }.flatten().forEach { ref ->
            assertEquals(ref, VerseRef(ref.book, ref.chapter))
        }
    }
}
