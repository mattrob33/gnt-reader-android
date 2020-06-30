package com.mattrobertson.greek.reader.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.mattrobertson.greek.reader.SblGntApplication
import com.mattrobertson.greek.reader.model.GntVerseRef
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsTest {

    private lateinit var context: Context
    private lateinit var settings: Settings

    @Before
    fun setup() {
        context = getApplicationContext<SblGntApplication>()
        settings = Settings.getInstance(context)
    }

    @Test
    fun `Recents saves & loads correctly`() {
        Recents.add(GntVerseRef(0, 5))
        Recents.add(GntVerseRef(1, 5))
        Recents.add(GntVerseRef(2, 5))
        Recents.add(GntVerseRef(3, 5))

        assertEquals(Recents.size, 4)

        settings.saveRecents()
        Recents.clear()

        assertEquals(Recents.size, 0)

        settings.loadRecents()

        assertEquals(Recents.size, 4)
        assertEquals(Recents.getAll()[3], GntVerseRef(0, 5))
    }
}