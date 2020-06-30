package com.mattrobertson.greek.reader.presentation.vocab

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattrobertson.greek.reader.data.DataBaseHelper
import com.mattrobertson.greek.reader.util.getFileName
import com.mattrobertson.greek.reader.util.readEntireFileFromAssets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ChapterVocabViewModel(private val applicationContext: Context,
                            private val book: Int,
                            private val chapter: Int
) : ViewModel() {

    private val _cursorVersion = MutableLiveData(0)
    val cursorVersion: LiveData<Int> = _cursorVersion

    private lateinit var dbHelper: DataBaseHelper

    init {
        try {
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

    fun deleteAllVocabForChapter() {
        dbHelper.opendatabase()
        val db: SQLiteDatabase = dbHelper.writableDatabase
        db.execSQL("DELETE FROM vocab WHERE book=$book AND chapter=$chapter")
        dbHelper.close()
    }

    fun autoBuildWordList(maxOcc: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val greekText = readEntireFileFromAssets(applicationContext.assets, getFileName(book))
            if (greekText.isNotBlank()) {
                val words = buildWordList(greekText, maxOcc)
                for (d in words) {
                    saveVocabWord(d.lex, d.def, d.occ)
                }

                viewModelScope.launch(Dispatchers.Main) {
                    _cursorVersion.value = _cursorVersion.value?.plus(1)
                }
            }
        }
    }

    private fun buildWordList(greekText: String, maxOcc: Int): ArrayList<DefInfo> {
        val words = ArrayList<DefInfo>()

        val arrWords: Array<String> = greekText.split("\n".toRegex()).toTypedArray()
        var arrLine: Array<String>
        val wordSet = HashSet<String>()

        var lex: String
        var ref: String

        var str: String
        for (n in arrWords.indices) {
            str = arrWords[n]
            arrLine = str.split(" ".toRegex()).toTypedArray()
            ref = if (arrLine.isNotEmpty()) arrLine[0] else ""
            lex = if (arrLine.size > 5) arrLine[6] else ""
            if (ref.length < 6) continue
            val curChap = ref.substring(2, 4).toInt()
            if (curChap < chapter) continue
            if (curChap > chapter) break
            if (wordSet.contains(lex)) continue
            wordSet.add(lex)

            getDef(lex)?.apply {
                if (occ in 1 until maxOcc) {
                    words.add(this)
                }
            }
        }

        return words
    }

    private fun getDef(lex: String): DefInfo? {
        if (lex.isEmpty()) return null
        dbHelper.opendatabase()
        val db = dbHelper.readableDatabase
        val c = db.rawQuery("SELECT * FROM words WHERE lemma='$lex'", null)
        if (!c.moveToFirst()) {
            // Temp hack to find missing words
            val c2 = db.rawQuery("SELECT * FROM glosses WHERE gk='$lex'", null)
            if (c2.moveToFirst()) {
                val gloss = c2.getString(c2.getColumnIndex("gloss"))
                val freq = c2.getInt(c2.getColumnIndex("occ"))
                c2.close()
                dbHelper.close()
                return DefInfo(lex, gloss, freq)
            }
        }
        else {
            val freq = c.getInt(c.getColumnIndex("freq"))
            val def = c.getString(c.getColumnIndex("def"))
            val gloss = c.getString(c.getColumnIndex("gloss"))
            val strDef = gloss ?: def
            c.close()
            dbHelper.close()
            return DefInfo(lex, strDef, freq)
        }
        return null
    }

    private fun saveVocabWord(lex: String?, def: String?, freq: Int) {
        dbHelper.opendatabase()
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("book", book)
            put("chapter", chapter)
            put("lex", lex)
            put("gloss", def)
            put("occ", freq)
            put("added_by", DataBaseHelper.ADDED_BY_WIZARD)
            put("date_added", System.currentTimeMillis())
            put("learned", 0)
        }
        db.insertWithOnConflict("vocab", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        db.close()
    }

    private data class DefInfo (
            val lex: String,
            val def: String,
            val occ: Int
    )
}