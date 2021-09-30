package com.mattrobertson.greek.reader.settings

import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class RecentsTest {

    @Nested
    @DisplayName("Given cleared list")
    inner class EmptyList {
        @Test
        @DisplayName("size is zero")
        fun `list is empty`() {
            Recents.add(newRef())
            Recents.clear()
            assertEquals(Recents.size, 0)
        }

        @Test
        @DisplayName("getAll() is empty")
        fun `getAll is empty`() {
            Recents.add(newRef())
            Recents.clear()
            assertEquals(Recents.getAll().size, 0)
        }
    }

    @Nested
    @DisplayName("Given list of Unique items")
    inner class UniqueItems {
        @Test
        @DisplayName("size is correct")
        fun `size is correct`() {
            Recents.clear()

            Recents.add(VerseRef(Book(0), 5))
            Recents.add(VerseRef(Book(2), 4))
            Recents.add(VerseRef(Book(5), 5))

            assertEquals(Recents.size, 3)
        }

        @Test
        @DisplayName("getAll() contains correct items")
        fun `getAll is correct`() {
            Recents.clear()

            Recents.add(VerseRef(Book(0), 5))
            Recents.add(VerseRef(Book(2), 4))
            Recents.add(VerseRef(Book(5), 5))

            val recents = Recents.getAll()
            assertEquals(recents[0], VerseRef(Book(5), 5))
            assertEquals(recents[1], VerseRef(Book(2), 4))
            assertEquals(recents[2], VerseRef(Book(0), 5))
        }

        @Nested
        @DisplayName("On remove()")
        inner class Remove {
            @Test
            @DisplayName("getAll() contains correct items")
            fun `remove leaves correct items`() {
                Recents.clear()

                Recents.add(VerseRef(Book(0), 5))
                Recents.add(VerseRef(Book(2), 4))
                Recents.add(VerseRef(Book(5), 5))

                Recents.remove(VerseRef(Book(2), 4))

                val recents = Recents.getAll()
                assertEquals(recents[0], VerseRef(Book(5), 5))
                assertEquals(recents[1], VerseRef(Book(0), 5))
            }

            @Test
            @DisplayName("size is correct")
            fun `remove leaves correct size`() {
                Recents.clear()

                Recents.add(VerseRef(Book(0), 5))
                Recents.add(VerseRef(Book(2), 4))
                Recents.add(VerseRef(Book(5), 5))

                Recents.remove(VerseRef(Book(2), 4))

                assertEquals(Recents.size, 2)
            }

            @Test
            @DisplayName("If item not contained, size is correct")
            fun `invalid remove leaves correct size`() {
                Recents.clear()

                Recents.add(VerseRef(Book(0), 5))
                Recents.add(VerseRef(Book(2), 4))
                Recents.add(VerseRef(Book(5), 5))

                Recents.remove(VerseRef(Book(1), 1))

                assertEquals(Recents.size, 3)
            }
        }

        @Nested
        @DisplayName("On removeAt()")
        inner class RemoveAt {
            @Test
            @DisplayName("getAll() contains correct items")
            fun `removeAt leaves correct items`() {
                Recents.clear()

                Recents.add(VerseRef(Book(0), 5))
                Recents.add(VerseRef(Book(2), 4))
                Recents.add(VerseRef(Book(5), 5))

                Recents.removeAt(1)

                val recents = Recents.getAll()
                assertEquals(recents[0], VerseRef(Book(5), 5))
                assertEquals(recents[1], VerseRef(Book(0), 5))
            }

            @Test
            @DisplayName("size is correct")
            fun `removeAt leaves correct size`() {
                Recents.clear()

                Recents.add(VerseRef(Book(0), 5))
                Recents.add(VerseRef(Book(2), 4))
                Recents.add(VerseRef(Book(5), 5))

                Recents.removeAt(2)

                assertEquals(Recents.size, 2)
            }

            @Test
            @DisplayName("If index is out of bounds, then exception is thrown")
            fun `illegal index throws exception`() {
                Recents.clear()

                Recents.add(VerseRef(Book(0), 5))
                Recents.add(VerseRef(Book(2), 4))
                Recents.add(VerseRef(Book(5), 5))

                assertThrows<IndexOutOfBoundsException> {
                    Recents.removeAt(28)
                }
            }
        }

        @Nested
        @DisplayName("On serialization")
        inner class Serialization {
            @Test
            @DisplayName("cleared & reloaded contents are correct")
            fun `reloaded contents are correct`() {
                Recents.clear()

                Recents.add(VerseRef(Book(0), 5))
                Recents.add(VerseRef(Book(2), 4))
                Recents.add(VerseRef(Book(5), 5))

                val json = Recents.toJson()

                Recents.clear()
                Recents.fromJson(json)

                val recents = Recents.getAll()
                assertEquals(recents[0], VerseRef(Book(5), 5))
                assertEquals(recents[1], VerseRef(Book(2), 4))
                assertEquals(recents[2], VerseRef(Book(0), 5))
            }
        }
    }

    @Nested
    @DisplayName("Given list of Non-unique items")
    inner class NonUniqueItems {
        @Test
        @DisplayName("size is correct")
        fun `size is correct`() {
            Recents.clear()

            Recents.add(VerseRef(Book(0), 5))
            Recents.add(VerseRef(Book(2), 4))
            Recents.add(VerseRef(Book(5), 5))
            Recents.add(VerseRef(Book(2), 4))
            Recents.add(VerseRef(Book(5), 5))
            Recents.add(VerseRef(Book(5), 5))
            Recents.add(VerseRef(Book(5), 5))
            Recents.add(VerseRef(Book(5), 5))
            Recents.add(VerseRef(Book(5), 5))
            Recents.add(VerseRef(Book(5), 5))

            assertEquals(Recents.size, 3)
        }

        @Test
        @DisplayName("getAll() is correct")
        fun `getAll is correct`() {
            Recents.clear()

            Recents.add(VerseRef(Book(0), 5))
            Recents.add(VerseRef(Book(2), 4))
            Recents.add(VerseRef(Book(5), 5))
            Recents.add(VerseRef(Book(2), 4))

            val recents = Recents.getAll()
            assertEquals(recents[0], VerseRef(Book(2), 4))
            assertEquals(recents[1], VerseRef(Book(5), 5))
            assertEquals(recents[2], VerseRef(Book(0), 5))
        }
    }

    private fun newRef() = VerseRef(
            Book(Random.nextInt(0, 27)),
            Random.nextInt(1, 2)
    )

}