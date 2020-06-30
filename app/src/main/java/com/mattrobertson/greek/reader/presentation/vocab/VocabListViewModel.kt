package com.mattrobertson.greek.reader.presentation.vocab

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.ViewModel
import com.mattrobertson.greek.reader.data.DataBaseHelper

class VocabListViewModel(applicationContext: Context) : ViewModel() {

    private lateinit var dbHelper: DataBaseHelper

    init {
        try {
            dbHelper = DataBaseHelper(applicationContext)
        } catch (e: Exception) {
            Log.e("sblgnt", "Unable to open database")
        }
    }

    fun getCursor(): Cursor {
        dbHelper.opendatabase()
        val query = "SELECT rowid as _id,book,chapter FROM vocab GROUP BY book,chapter ORDER BY book,chapter"
        val db = dbHelper.readableDatabase
        return db.rawQuery(query, null)
    }

    fun getBookAndChapter(rowId: Long): Pair<Int, Int> {
        dbHelper.opendatabase()
        val query = "SELECT book,chapter FROM vocab WHERE rowid=$rowId"
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val c = db.rawQuery(query, null)
        c.moveToFirst()
        val book = c.getInt(c.getColumnIndex("book"))
        val chapter = c.getInt(c.getColumnIndex("chapter"))
        dbHelper.close()
        return Pair(book, chapter)
    }
}