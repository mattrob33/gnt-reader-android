package com.mattrobertson.greek.reader.presentation.reader

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.repo.VerseRepo

class ReaderViewModelFactory(
        private val applicationContext: Context,
        private val verseRepo: VerseRepo,
        private val book: Book,
        private val chapter: Int
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReaderViewModel(applicationContext, verseRepo, book, chapter) as T
    }

}