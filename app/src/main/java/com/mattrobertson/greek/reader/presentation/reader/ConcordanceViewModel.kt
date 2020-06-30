package com.mattrobertson.greek.reader.presentation.reader

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.model.GntVerseRef
import com.mattrobertson.greek.reader.presentation.util.ConcordanceWordSpan
import com.mattrobertson.greek.reader.data.DataBaseHelper
import com.mattrobertson.greek.reader.presentation.util.ScreenState
import com.mattrobertson.greek.reader.presentation.util.SingleLiveEvent
import com.mattrobertson.greek.reader.util.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class ConcordanceViewModel (private val applicationContext: Context) : ViewModel() {

    companion object {
        private const val PROGRESS_UPDATE_FREQUENCY = 16
    }

    private var mLex = ""

    var showText = false
        set(value) {
            field = value
            loadConcordance(mLex)
        }

    private val _screenState = MutableLiveData<ScreenState>(ScreenState.LOADING)
    val screenState: LiveData<ScreenState> = _screenState

    private val _loadingProgress = MutableLiveData(0)
        val loadingProgress: LiveData<Int> = _loadingProgress

    private val _concordanceInfo = MutableLiveData<SpannableStringBuilder?>()
        val concordanceInfo: LiveData<SpannableStringBuilder?> = _concordanceInfo

    var verseRefSelected = SingleLiveEvent<GntVerseRef?>()
        private set

    private lateinit var dbHelper: DataBaseHelper
    private lateinit var db: SQLiteDatabase

    init {
        try {
            dbHelper = DataBaseHelper(applicationContext)
            dbHelper.opendatabase()
            db = dbHelper.readableDatabase
        }
        catch (e: IOException) {
            // TODO: Log exception
        }
    }

    fun loadConcordance(lex: String) {
        _loadingProgress.value = 0
        _screenState.value = ScreenState.LOADING

        viewModelScope.launch(Dispatchers.IO) {
            val concord = lookupConcordance(lex)

            viewModelScope.launch(Dispatchers.Main) {
                _concordanceInfo.value = concord
                _screenState.value = ScreenState.READY
            }
        }
    }

    private fun lookupConcordance(lex: String): SpannableStringBuilder? {
        if (lex.isEmpty())
            return null

        mLex = lex

        val sb = SpannableStringBuilder()

        val c: Cursor = db.rawQuery("SELECT * FROM concordance WHERE lex='$lex'", null)
        var cVerse: Cursor

        var strVerse: String
        var strWord: String

        var i = 0
        val size = c.count

        var strLine: String
        var totalLength = 0

        var span: ConcordanceWordSpan
        var boldSpan: StyleSpan

        val linkColor = ResourcesCompat.getColor(applicationContext.resources, R.color.accent, applicationContext.theme)

        while (c.moveToNext()) {
            val book = c.getInt(c.getColumnIndex("book"))
            val chapter = c.getInt(c.getColumnIndex("chapter"))
            val verse = c.getInt(c.getColumnIndex("verse"))
            i++

            if (showText) {
                if (i % PROGRESS_UPDATE_FREQUENCY == 0)
                    viewModelScope.launch(Dispatchers.Main) {
                        _loadingProgress.value = (i.toDouble() / size.toDouble() * 100).toInt()
                        _concordanceInfo.value = sb
                    }
            }

            strLine = "$i. ${AppConstants.abbrvs[book]} $chapter:$verse\n"
            sb.append(strLine)
            span = object : ConcordanceWordSpan(book, chapter, verse, linkColor) {
                override fun onClick(v: View) {
                    verseRefSelected.value = GntVerseRef(book, chapter, verse)
                }
            }

            sb.setSpan(span, totalLength, totalLength + strLine.length - 1, Spanned.SPAN_COMPOSING)
            totalLength += strLine.length

            if (showText) {
                cVerse = db.rawQuery("SELECT morph,lex FROM concordance WHERE book='$book' AND chapter='$chapter' AND verse='$verse'", null)
                while (cVerse.moveToNext()) {
                    strWord = cVerse.getString(0)
                    strVerse = "$strWord "
                    sb.append(strVerse)

                    if (lex == cVerse.getString(1)) {
                        boldSpan = StyleSpan(Typeface.BOLD)
                        sb.setSpan(boldSpan, totalLength, totalLength + strWord.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                    totalLength += strVerse.length
                }
                cVerse.close()
                sb.append("\n\n")
                totalLength += 2
            }
        }
        c.close()
        return sb
    }

}