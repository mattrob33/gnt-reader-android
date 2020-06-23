package com.mattrobertson.greek.reader.presentation.vocab

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VocabListViewModelFactory(private val applicationContext: Context): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VocabListViewModel(applicationContext) as T
    }

}