package com.mattrobertson.greek.reader.plans

import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef

data class ReadingPlan(
    val id: String,
    val title: String,
    val description: String,
    val days: List<List<VerseRef>>
)

object ReadingPlans {
    private val wallaceBookOrder = listOf(
        Book.JOHN, Book.FIRST_JOHN, Book.SECOND_JOHN, Book.THIRD_JOHN, Book.PHILEMON,
        Book.REVELATION, Book.FIRST_THESSALONIANS, Book.SECOND_THESSALONIANS,
        Book.PHILIPPIANS, Book.MARK, Book.MATTHEW, Book.ROMANS, Book.EPHESIANS,
        Book.COLOSSIANS, Book.GALATIANS, Book.JAMES, Book.FIRST_CORINTHIANS, Book.SECOND_CORINTHIANS,
        Book.FIRST_TIMOTHY, Book.SECOND_TIMOTHY, Book.TITUS, Book.FIRST_PETER,
        Book.SECOND_PETER, Book.JUDE, Book.LUKE, Book.ACTS, Book.HEBREWS
    )

    private val wallaceChapters = wallaceBookOrder.flatMap(::chapters)

    val all: List<ReadingPlan> = listOf(
        ReadingPlan(
            id = "nt_year_wallace",
            title = "NT in 1 Year (by Dan Wallace)",
            description = "Read through the NT in a year (taking weekends off) with the ‘revolving door’ principle. Each day adds one new chapter and reviews the previous day's chapter. Organized by difficulty and genre. Developed by Greek professor Dan Wallace.",
            days = wallaceChapters.indices.map { day ->
                wallaceChapters.subList(maxOf(0, day - 1), day + 1)
            }
        ),
        ReadingPlan(
            id = "nt_month_wallace",
            title = "NT in 1 Month (by Dan Wallace)",
            description = "Read through the NT in 28 days by reading about 10 chapters each day. ‘Not for the faint of heart.’ Organized by difficulty and genre. Developed by Greek professor Dan Wallace.",
            days = wallaceChapters.chunkBy(
                listOf(11, 10, 8, 11, 11, 8, 10, 10, 10, 10, 8, 8, 8, 10,
                    11, 10, 10, 9, 13, 9, 8, 8, 8, 10, 9, 9, 7, 6)
            )
        ),
        ReadingPlan(
            id = "gospels_three_months",
            title = "Gospels in 3 months",
            description = "Read the four gospels in 3 months (89 days), reading one chapter each day.",
            days = listOf(Book.MATTHEW, Book.MARK, Book.LUKE, Book.JOHN)
                .flatMap(::chapters).map(::listOf)
        ),
        ReadingPlan(
            id = "romans_month",
            title = "Romans in 1 Month",
            description = "Read Romans in 31 days, reading a new chapter every two days.",
            days = chapters(Book.ROMANS).flatMapIndexed { index, ref ->
                if (index == 15) listOf(listOf(ref)) else listOf(listOf(ref), listOf(ref))
            }
        ),
        ReadingPlan(
            id = "prison_epistles",
            title = "Prison Epistles in 15 days",
            description = "Read Paul's ‘prison epistles’ in 15 days, reading a chapter each day.",
            days = listOf(Book.PHILIPPIANS, Book.EPHESIANS, Book.COLOSSIANS, Book.PHILEMON)
                .flatMap(::chapters).map(::listOf)
        ),
        ReadingPlan(
            id = "general_epistles",
            title = "General Epistles in 3 weeks",
            description = "Read through the General Epistles in 3 weeks, reading a chapter each day.",
            days = listOf(Book.JAMES, Book.FIRST_PETER, Book.SECOND_PETER, Book.FIRST_JOHN,
                Book.SECOND_JOHN, Book.THIRD_JOHN, Book.JUDE)
                .flatMap(::chapters).map(::listOf)
        )
    )

    private fun chapters(book: Book): List<VerseRef> {
        val result = mutableListOf<VerseRef>()
        var chapter = 1
        while (true) {
            try {
                result += VerseRef(book, chapter++)
            } catch (_: IllegalArgumentException) {
                return result
            }
        }
    }

    private fun <T> List<T>.chunkBy(sizes: List<Int>): List<List<T>> {
        require(sizes.sum() == size)
        var offset = 0
        return sizes.map { count -> subList(offset, offset + count).also { offset += count } }
    }
}
