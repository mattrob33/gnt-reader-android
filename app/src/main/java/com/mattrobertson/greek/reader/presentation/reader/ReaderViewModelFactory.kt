package com.mattrobertson.greek.reader.presentation.reader

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReaderViewModelFactory(
        private val applicationContext: Context,
        private val book: Int,
        private val chapter: Int
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReaderViewModel(applicationContext, book, chapter) as T
    }

}