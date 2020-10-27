package com.mattrobertson.greek.reader.presentation.reader

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.data.DataBaseHelper
import com.mattrobertson.greek.reader.data.Recents
import com.mattrobertson.greek.reader.data.Settings
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.GlossInfo
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.presentation.util.ConcordanceWordSpan
import com.mattrobertson.greek.reader.presentation.util.ScreenState
import com.mattrobertson.greek.reader.presentation.util.SingleLiveEvent
import com.mattrobertson.greek.reader.presentation.util.WordSpan
import com.mattrobertson.greek.reader.repo.VerseRepo
import com.mattrobertson.greek.reader.util.AppConstants
import com.mattrobertson.greek.reader.util.getBookTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReaderViewModel(
    private val applicationContext: Context,
    private val verseRepo: VerseRepo,
    private var book: Book,
    private var chapter: Int
) : ViewModel() {

    private val settings = Settings.getInstance(applicationContext)

    private val _state = MutableLiveData<ScreenState>()
    val state: LiveData<ScreenState> = _state

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _spannedText = MutableLiveData<SpannableStringBuilder>()
    val spannedText: LiveData<SpannableStringBuilder> = _spannedText

    private val _html = MutableLiveData<String>()
    val html: LiveData<String> = _html

    private val _selectedWordId = MutableLiveData(-1)
    var selectedWordId: LiveData<Int> = _selectedWordId

    private val _selectedWord = MutableLiveData<Word>()
    var selectedWord: LiveData<Word> = _selectedWord

    private val _glossInfo = MutableLiveData<GlossInfo?>()
    var glossInfo: LiveData<GlossInfo?> = _glossInfo

    private val _concordanceInfo = MutableLiveData<SpannableStringBuilder?>()
    var concordanceInfo: LiveData<SpannableStringBuilder?> = _concordanceInfo

    var concordanceItemSelected = SingleLiveEvent<VerseRef?>()
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

    private val refBackstack = arrayListOf<VerseRef>()

    var hasScrolled = false

    init {
        try {
            dbHelper = DataBaseHelper(applicationContext)
        } catch (e: Exception) {
            // TODO : log exception
        }

        loadChapter(book, chapter)

        addToRecents(VerseRef(book, chapter))

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
            goTo(VerseRef(toRef.book, toRef.chapter, toRef.verse))
            true
        }
    }

    private fun goTo(ref: VerseRef) {
        _selectedWordId.value = -1
        chapter = ref.chapter

        loadChapter(ref.book, ref.chapter)

        addToRecents(VerseRef(book, chapter))
    }

    private fun loadChapter(newBook: Book, chapter: Int) {
        _state.value = ScreenState.LOADING

        hasScrolled = false

        book = newBook

        viewModelScope.launch(Dispatchers.IO) {
            val verses = verseRepo.getVersesForBook(book)

            val htmlBuilder = StringBuilder()

            var wordIndex = 0
            var lastVerseNum = 0
            var lastChapterNum = 0

            showVerseNumbers = true

            var prevWasEndOfSentence = false

            verses.forEach { verse ->
                val chapterNum = verse.verseRef.chapter
                val verseNum = verse.verseRef.verse

                if (chapterNum != lastChapterNum) {
                    if (chapterNum > 1)
                        htmlBuilder.append("</p><br>")

                    htmlBuilder
                        .append("<p style=\"text-indent: 3px;\">")
                        .append("<span style=\"font-size: x-large; font-weight: bold; outline-style: double;\"><a name=\"${verse.verseRef.book.num}_${verse.verseRef.chapter}_${verse.verseRef.verse}\">&nbsp;$chapterNum&nbsp;</a></span>&nbsp;&nbsp;")
                }

                lastChapterNum = chapterNum

                verse.words.forEach { word ->

                    val isUppercase = word.text.first().toUpperCase() == word.text.first()

                    // Paragraph divisions
                    if (isUppercase && prevWasEndOfSentence && verseNum > 1) {
                        htmlBuilder.append("<p style=\"text-indent: 1em;\">")
                    }

                    prevWasEndOfSentence = word.text.last() == '.'

                     // Verse numbers
                    if (verseNum != lastVerseNum) {
//                        if (showVersesNewLines) {
//                            if (verseNum > 1)
//                                htmlBuilder.append("</p>")
//                            htmlBuilder.append("<p>")
//                        }
                        if (showVerseNumbers) {
                            htmlBuilder.append("<sup style=\"font-size:0.65em;\">$verseNum</sup>")
                        }
                        lastVerseNum = verseNum
                    }

                    htmlBuilder.append("${word.text} ")

                    wordIndex++

                    words.add(word)
                }
            }

            htmlBuilder.append("</p>")

            viewModelScope.launch(Dispatchers.Main) {
                _state.value = ScreenState.READY

                val htmlString = htmlBuilder.toString()
                _html.value = htmlString
            }
        }

        _title.value = getBookTitle(book) + " " + chapter

//        audio.stop()
//        refreshAudioUI()
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
        val lex = word.lexicalForm
        val parsing = word.parsing.humanReadable

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
            glossInfo = GlossInfo(lex = lex, gloss = strDef, parsing = parsing, frequency = freq)
        }
        c.close()

        return glossInfo
    }

    private fun showConcordance(word: Word) {
        _concordanceInfo.value = lookupConcordance(word)
    }

    private fun lookupConcordance(word: Word): SpannableStringBuilder? {
        val lex = word.lexicalForm

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

        val linkColor = ResourcesCompat.getColor(applicationContext.resources, R.color.accent, applicationContext.theme)

        while (c.moveToNext()) {
            val book = c.getInt(c.getColumnIndex("book"))
            val chapter = c.getInt(c.getColumnIndex("chapter"))
            val verse = c.getInt(c.getColumnIndex("verse"))

            i++

            strLine = "$i. ${AppConstants.abbrvs[book]} $chapter:$verse\n"

            sb.append(strLine)

            span = object : ConcordanceWordSpan(book, chapter, verse, linkColor) {
                override fun onClick(v: View) {
                    refBackstack.add(VerseRef(this@ReaderViewModel.book, this@ReaderViewModel.chapter))
                    goTo(VerseRef(Book(book), chapter, verse))
                }
            }

            sb.setSpan(span, totalLength, totalLength + strLine.length - 1, Spanned.SPAN_COMPOSING)

            totalLength += strLine.length

            if (i >= 10) {
                if (size > 10) {
                    val strMore = "..." + (size - i) + " more"
                    sb.append(strMore)
                    val spanMore: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(v: View) {
                            showConcordanceScreenForLex.value = lex
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = linkColor
                            ds.isUnderlineText = false
                        }
                    }
                    sb.setSpan(spanMore, totalLength, totalLength + strMore.length, Spanned.SPAN_COMPOSING)
                }
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
            put("book", book.num)
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

    private fun addToRecents(ref: VerseRef) {
        Recents.add(ref)
        settings.saveRecents()
    }
}