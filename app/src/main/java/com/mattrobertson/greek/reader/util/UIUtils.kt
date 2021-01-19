package com.mattrobertson.greek.reader.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.ColorInt
import kotlin.math.round

// UI Utils

fun dpToPx(context: Context, dp: Float): Float {
	val displayMetrics: DisplayMetrics = context.resources.displayMetrics
	return round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun dpToPx(context: Context, dp: Int): Int = dpToPx(context, dp.toFloat()).toInt()

@ColorInt
fun getThemedColor(context: Context, resId: Int): Int {
	val typedValue = TypedValue()
	val theme: Resources.Theme = context.theme
	theme.resolveAttribute(resId, typedValue, true)
	return typedValue.data
}