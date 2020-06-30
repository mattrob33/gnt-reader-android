package com.mattrobertson.greek.reader.presentation.reader

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattrobertson.greek.reader.data.DataBaseHelper
import com.mattrobertson.greek.reader.data.Recents
import com.mattrobertson.greek.reader.data.Settings
import com.mattrobertson.greek.reader.model.GntVerseRef
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.presentation.util.ConcordanceWordSpan
import com.mattrobertson.greek.reader.presentation.util.ScreenState
import com.mattrobertson.greek.reader.presentation.util.SingleLiveEvent
import com.mattrobertson.greek.reader.presentation.util.WordSpan
import com.mattrobertson.greek.reader.util.AppConstants
import com.mattrobertson.greek.reader.util.getFileName
import com.mattrobertson.greek.reader.util.readEntireFileFromAssets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ReaderViewModel(
        private val applicationContext: Context,
        private var book: Int,
        private var chapter: Int
) : ViewModel() {

    private val settings = Settings.getInstance(applicationContext)

    private val _state = MutableLiveData<ScreenState>()
        val state: LiveData<ScreenState> = _state

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _spannedText = MutableLiveData<SpannableStringBuilder>()
        val spannedText: LiveData<SpannableStringBuilder> = _spannedText

    private val _selectedWordId = MutableLiveData(-1)
        var selectedWordId: LiveData<Int> = _selectedWordId

    private val _selectedWord = MutableLiveData<Word>()
        var selectedWord: LiveData<Word> = _selectedWord

    private val _glossInfo = MutableLiveData<GlossInfo?>()
        var glossInfo: LiveData<GlossInfo?> = _glossInfo

    private val _concordanceInfo = MutableLiveData<SpannableStringBuilder?>()
        var concordanceInfo: LiveData<SpannableStringBuilder?> = _concordanceInfo

    var concordanceItemSelected = SingleLiveEvent<GntVerseRef?>()
        private set

    var showConcordanceScreenForLex = SingleLiveEvent<String>()
        private set

    private lateinit var dbHelper: DataBaseHelper

    private var words = ArrayList<Word>()
    private var wordSpans = ArrayList<WordSpan>()

    private var showVerseNumbers = false
    private var showVersesNewLines = false
    private var showAudioBtn = false

    private val greekFont = Typeface.createFromAsset(applicationContext.assets, "fonts/sblgreek.ttf")
    private var fontSize = 0

    private val refBackstack = arrayListOf<GntVerseRef>()

    init {
        try {
            dbHelper = DataBaseHelper(applicationContext)
        } catch (e: Exception) {
            // TODO : log exception
        }

        addToRecents(GntVerseRef(book, chapter))

        loadBook(book)

        selectedWord.observeForever { word ->
            showGloss(word)
            showConcordance(word)
        }
    }

    @ExperimentalStdlibApi
    fun navigateBack(): Boolean {
        return if(refBackstack.isEmpty()) {
            false
        }
        else {
            val toRef = refBackstack.removeLast()
            goTo(toRef)
            true
        }
    }

    private fun goTo(ref: GntVerseRef) {
        _selectedWordId.value = -1
        chapter = ref.chapter
        loadBook(ref.book)

        addToRecents(GntVerseRef(book, chapter))
    }

    private fun loadBook(newBook: Int) {
        _state.value = ScreenState.LOADING

        book = newBook

        viewModelScope.launch(Dispatchers.IO) {
            val rawFileText = readBookContents()

            if (rawFileText.isNotBlank()) {
                val text = processRawFileText(rawFileText)

                viewModelScope.launch(Dispatchers.Main) {
                    _state.value = ScreenState.READY
                    _spannedText.value = text
                }
            }
        }

        _title.value = AppConstants.abbrvs[book] + " " + chapter

//        audio.stop()
//        refreshAudioUI()
    }

    private fun readBookContents() = readEntireFileFromAssets(applicationContext.assets, getFileName(book))

    @SuppressLint("DefaultLocale")
    private fun processRawFileText(rawFileText: String): SpannableStringBuilder {
        val arrWords: Array<String> = rawFileText.split("\n".toRegex()).toTypedArray()

        var index: Int

        var arrLine: Array<String>

        words = ArrayList()
        wordSpans = ArrayList()

        var x: String
        var r: String
        var l: String
        var p: String
        var str: String
        var word: Word

        var totalLength = 0
        var lastVerse = 0

        val spannableStringBuilder = SpannableStringBuilder()
        var curSpan: WordSpan

        for (arrWord in arrWords) {
            str = arrWord
            arrLine = str.split(" ".toRegex()).toTypedArray()
            r = if (arrLine.isNotEmpty()) arrLine[0] else ""
            x = if (arrLine.size > 2) arrLine[3] else ""
            p = if (arrLine.size > 1) arrLine[1] + " " + arrLine[2] else ""
            l = if (arrLine.size > 5) arrLine[6] else ""
            if (r.length < 6) continue
            val curChap = r.substring(2, 4).toInt()
            val curVerse = r.substring(4).toInt()
            if (curChap < chapter) continue
            if (curChap > chapter) break
            word = Word(words.size, x, r, l, p)
            index = words.size
            words.add(word)
            val isUppercase = word.toString().substring(0, 1).toUpperCase() == word.toString().substring(0, 1)

            // Paragraph divisions
            if (words.size > 1) {
                val lastWord: String = words[index - 1].toString().trim()
                if (lastWord.contains(".") && isUppercase) {
                    spannableStringBuilder.append("\n\t\t\t\t\t")
                    totalLength += 6
                }
            } else if (isUppercase) {
                spannableStringBuilder.append("\t\t\t\t\t")
                totalLength += 5
            }

            // Verse numbers
            if (curVerse > lastVerse) {
                if (showVersesNewLines) {
                    spannableStringBuilder.append("\n")
                    totalLength += 1
                }
                if (showVerseNumbers) {
                    val strVerse = "" + curVerse + ""
                    spannableStringBuilder.append(strVerse)
                    spannableStringBuilder.setSpan(SuperscriptSpan(), totalLength, totalLength + strVerse.length, Spanned.SPAN_COMPOSING)
                    spannableStringBuilder.setSpan(RelativeSizeSpan(0.65f), totalLength, totalLength + strVerse.length, Spanned.SPAN_COMPOSING)
                    totalLength += strVerse.length
                }
                lastVerse = curVerse
            }
            spannableStringBuilder.append(x).append(" ")
            val _index = index
            curSpan = object : WordSpan(_index, greekFont, _index == selectedWordId.value) {
                override fun onClick(view: View) {
                    viewModelScope.launch(Dispatchers.Main) {
                        handleWordClick(_index)
                        setMarking(true)
                    }
                }
            }
            spannableStringBuilder.setSpan(curSpan, totalLength, totalLength + word.toString().length, Spanned.SPAN_COMPOSING)
            totalLength += word.toString().length + 1
            wordSpans.add(curSpan)
        }

        return spannableStringBuilder
    }

    fun handleWordClick(id: Int) {
        val prevId = selectedWordId.value!!
        if (prevId in wordSpans.indices) {
            wordSpans[prevId].setMarking(false)
        }

        _selectedWordId.value = id
        _selectedWord.value = words[id]
    }

    private fun showGloss(word: Word) {
        val glossInfo = lookupGloss(word)
        _glossInfo.value = glossInfo
        glossInfo?.let {
            saveVocabWord(it)
        }
    }

    private fun lookupGloss(word: Word): GlossInfo? {
        val lex = word.lex
        val parsing = word.parsing

        if (lex.isBlank()) return null

        dbHelper.opendatabase()

        val db: SQLiteDatabase = dbHelper.readableDatabase

        var glossInfo: GlossInfo? = null

        val c = db.rawQuery("SELECT * FROM words WHERE lemma='$lex'", null)

        if (!c.moveToFirst()) {
            // Temp hack to find missing words
            val c2 = db.rawQuery("SELECT * FROM glosses WHERE gk='$lex'", null)
            if (c2.moveToFirst()) {
                val gloss = c2.getString(c2.getColumnIndex("gloss"))
                val freq = c2.getInt(c2.getColumnIndex("occ"))
                glossInfo = GlossInfo(lex = lex, gloss = gloss, parsing = parsing, frequency = freq)
            }
            c2.close()
        } else {
            val freq = c.getInt(c.getColumnIndex("freq"))
            val def = c.getString(c.getColumnIndex("def"))
            val gloss = c.getString(c.getColumnIndex("gloss"))
            val strDef = gloss ?: def
            glossInfo = GlossInfo(lex = lex, gloss=strDef, parsing = parsing, frequency = freq)
        }
        c.close()

        return glossInfo
    }

    private fun showConcordance(word: Word) {
        _concordanceInfo.value = lookupConcordance(word)
    }

    private fun lookupConcordance(word: Word): SpannableStringBuilder? {
        val lex = word.lex

        if (lex.isBlank()) return null

        dbHelper.opendatabase()
        val db = dbHelper.readableDatabase
        val c = db.rawQuery("SELECT * FROM concordance WHERE lex='$lex'", null)

        var i = 0
        val size = c.count
        var strLine: String
        var totalLength = 0

        val sb = SpannableStringBuilder()
        var span: ConcordanceWordSpan

        while (c.moveToNext()) {
            val book = c.getInt(c.getColumnIndex("book"))
            val chapter = c.getInt(c.getColumnIndex("chapter"))
            val verse = c.getInt(c.getColumnIndex("verse"))

            i++

            strLine = "$i. ${AppConstants.abbrvs[book]} $chapter:$verse\n"

            sb.append(strLine)

            span = object : ConcordanceWordSpan(book, chapter, verse) {
                override fun onClick(v: View) {
                    refBackstack.add(GntVerseRef(this@ReaderViewModel.book, this@ReaderViewModel.chapter, 0))
                    goTo(GntVerseRef(book, chapter, verse))
                }
            }

            sb.setSpan(span, totalLength, totalLength + strLine.length - 1, Spanned.SPAN_COMPOSING)

            totalLength += strLine.length

            if (i >= 10) {
                val strMore = "..." + (size - i) + " more"
                sb.append(strMore)
                val spanMore: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(v: View) {
                        showConcordanceScreenForLex.value = lex
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = Color.parseColor("#0D47A1")
                        ds.isUnderlineText = false
                    }
                }
                sb.setSpan(spanMore, totalLength, totalLength + strMore.length, Spanned.SPAN_COMPOSING)
                break
            }
        }

        c.close()
        return sb
    }

    private fun saveVocabWord(glossInfo: GlossInfo) {
        dbHelper.opendatabase()
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("book", book)
            put("chapter", chapter)
            put("lex", glossInfo.lex)
            put("gloss", glossInfo.gloss)
            put("occ", glossInfo.frequency)
            put("added_by", DataBaseHelper.ADDED_BY_USER)
            put("date_added", System.currentTimeMillis())
            put("learned", 0)
        }

        db.insertWithOnConflict("vocab", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        db.close()
    }

    private fun addToRecents(ref: GntVerseRef) {
        Recents.add(ref)
        settings.saveRecents()
    }
}