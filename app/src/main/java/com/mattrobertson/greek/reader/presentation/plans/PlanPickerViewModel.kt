package com.mattrobertson.greek.reader.presentation.plans

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.mattrobertson.greek.reader.AppConstants

class PlanPickerViewModel(applicationContext: Context) : ViewModel() {

    val plans: Array<String> = AppConstants.READING_PLAN_TITLES

    private val prefs = getDefaultSharedPreferences(applicationContext)

    fun hasStartedPlan(plan: Int): Boolean {
        return prefs.getInt("plan-$plan-day", -1) >= 0
    }
}