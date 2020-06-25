package com.mattrobertson.greek.reader.presentation.reader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mattrobertson.greek.reader.objects.Word
import com.mattrobertson.greek.reader.objects.WordSpan
import com.mattrobertson.greek.reader.util.getFileName
import com.mattrobertson.greek.reader.util.readEntireFileFromAssets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ReaderViewModel(
        private val applicationContext: Context,
        private var book: Int,
        private val chapter: Int
) : ViewModel() {

    private val _state = MutableLiveData<ReaderState>()
    val state: LiveData<ReaderState> = _state

    private val _spannedText = MutableLiveData<SpannableStringBuilder>()
    val spannedText: LiveData<SpannableStringBuilder> = _spannedText

    private val _selectedWordId = MutableLiveData(-1)
    var selectedWordId: LiveData<Int> = _selectedWordId

    private val _selectedWord = MutableLiveData<Word>()
    var selectedWord: LiveData<Word> = _selectedWord

    private var words = ArrayList<Word>()
    private var wordSpans = ArrayList<WordSpan>()

    private var showVerseNumbers = false
    private var showVersesNewLines = false
    private var showAudioBtn = false

    private val greekFont = Typeface.createFromAsset(applicationContext.assets, "fonts/sblgreek.ttf")
    private var fontSize = 0

    init {
        loadBook(book)
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

    private fun loadBook(newBook: Int) {
        _state.value = ReaderState.LOADING

        book = newBook

        viewModelScope.launch(Dispatchers.IO) {
            val rawFileText = readBookContents()

            if (rawFileText.isNotBlank()) {
                val text = processRawFileText(rawFileText)

                viewModelScope.launch(Dispatchers.Main) {
                    _state.value = ReaderState.READY
                    _spannedText.value = text
                }
            }
        }

//        val refStr = AppConstants.abbrvs[book] + " " + chapter
//        setTitle(refStr)
//        audio.stop()
//        refreshAudioUI()
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)

//        selectedWordId = -1

//        val async: ReaderActivity.AsyncFileReader = ReaderActivity.AsyncFileReader()
//        async.execute()
    }

    private fun doNewChapter() {
        _state.value = ReaderState.LOADING

        // TODO : save to recents list

//        val refStr = AppConstants.abbrvs[book] + " " + chapter
//        setTitle(refStr)
//        audio.stop()
//        refreshAudioUI()
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)

//        selectedWordId = -1

//        val async: ReaderActivity.AsyncGreekTextProcessor = ReaderActivity.AsyncGreekTextProcessor()
//        async.execute()
    }

    fun handleWordClick(id: Int) {
        _selectedWord.value = words[id]

        val lex = words[id].lex
        val parsing = words[id].parsing

        // mark last click set false
        if (selectedWordId.value!! > -1) {
            wordSpans[selectedWordId.value!!].setMarking(false)
        }
        _selectedWordId.value = id

//        lookupDef(lex, parsing)
//        lookupConcordance(lex)
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
    }
}