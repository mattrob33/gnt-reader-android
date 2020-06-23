package com.mattrobertson.greek.reader.presentation.plans

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlanSplashViewModelFactory(private val plan: Int): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlanSplashViewModel(plan) as T
    }

}