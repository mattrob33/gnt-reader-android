package com.mattrobertson.greek.reader.ui

import android.webkit.JavascriptInterface
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.model.WordParsing
import com.mattrobertson.greek.reader.presentation.reader.ReaderViewModel

class ReaderJsInterface(private val viewModel: ReaderViewModel) {
	@JavascriptInterface
	fun onWordClick(text: String, lexicalForm: String, codedParsing: String) {
		val word = Word(text, lexicalForm, WordParsing.decode(codedParsing))
		viewModel.apply {
			showGloss(word)
			showConcordance(word)
		}
	}
}