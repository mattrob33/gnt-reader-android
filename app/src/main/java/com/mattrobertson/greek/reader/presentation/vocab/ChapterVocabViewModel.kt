package com.mattrobertson.greek.reader.presentation.vocab

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.ViewModel
import com.mattrobertson.greek.reader.objects.DataBaseHelper

class ChapterVocabViewModel(applicationContext: Context,
                            private val book: Int,
                            private val chapter: Int
) : ViewModel() {

    private lateinit var dbHelper: DataBaseHelper

    init {
        try
        {
            dbHelper = DataBaseHelper(applicationContext)
        }
        catch(e: Exception) {
            Log.e("sblgnt", e.message ?: "")
        }
    }

    fun getCursor(): Cursor {
        dbHelper.opendatabase()
        val query = "SELECT rowid _id,lex,gloss,occ,learned FROM vocab WHERE book=$book AND chapter=$chapter ORDER BY occ DESC"
        val db = dbHelper.readableDatabase
        return db.rawQuery(query, null)
    }

    fun deleteVocabWord(lex: String) {
        dbHelper.opendatabase()
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val args = arrayOf(lex)
        db.delete("vocab", "lex=?", args)
        dbHelper.close()
    }

}