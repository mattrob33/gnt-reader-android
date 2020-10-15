package com.mattrobertson.greek.reader.presentation.concordance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ConcordanceViewModelFactory(
        private val applicationContext: Context
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ConcordanceViewModel(applicationContext) as T
    }

}