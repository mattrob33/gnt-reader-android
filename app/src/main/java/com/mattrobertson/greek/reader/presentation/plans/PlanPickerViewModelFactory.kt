package com.mattrobertson.greek.reader.presentation.plans

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlanPickerViewModelFactory(private val applicationContext: Context): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlanPickerViewModel(applicationContext) as T
    }

}