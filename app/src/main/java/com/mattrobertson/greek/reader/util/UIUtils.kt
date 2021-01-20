package com.mattrobertson.greek.reader.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.mattrobertson.greek.reader.SblGntApplication

// UI Utils

fun dpToPx(dp: Float): Float {
	val displayMetrics = SblGntApplication.context.resources.displayMetrics
	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
}

fun dpToPx(dp: Int): Int = dpToPx(dp.toFloat()).toInt()

fun spToPx(sp: Float): Float {
	val displayMetrics = SblGntApplication.context.resources.displayMetrics
	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics)
}

fun spToPx(sp: Int): Int = spToPx(sp.toFloat()).toInt()

@ColorInt
fun getThemedColor(context: Context, resId: Int): Int {
	val typedValue = TypedValue()
	val theme: Resources.Theme = context.theme
	theme.resolveAttribute(resId, typedValue, true)
	return typedValue.data
}