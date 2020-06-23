package com.mattrobertson.greek.reader.presentation.plans

import androidx.lifecycle.ViewModel
import com.mattrobertson.greek.reader.AppConstants
import java.lang.StringBuilder

class PlanSplashViewModel(private val plan: Int): ViewModel() {

    val planTitle: String = AppConstants.READING_PLAN_TITLES[plan]
    val planDesc: String = AppConstants.READING_PLAN_DESCS[plan]
    val numDays: Int = AppConstants.READING_PLANS[plan].size

    fun getPreviewForDay(day: Int): String {
        val books = AppConstants.READING_PLANS[plan][day][0]
        val chapters = AppConstants.READING_PLANS[plan][day][1]

        val previewSb = StringBuilder()
        for (i in books.indices) {
            previewSb.append("${AppConstants.abbrvs[books[i]]} ${chapters[i]} ${System.getProperty("line.separator")}")
        }
        return previewSb.toString()
    }
}