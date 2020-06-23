package com.mattrobertson.greek.reader.util

import android.content.Context
import android.util.DisplayMetrics
import com.mattrobertson.greek.reader.AppConstants
import kotlin.math.round


val verses = arrayOf(
        intArrayOf(25, 23, 17, 25, 48, 34, 29, 34, 38, 42, 30, 50, 58, 36, 39, 28, 27, 35, 30, 34, 46, 46, 39, 51, 46, 75, 66, 20),
        intArrayOf(45, 28, 35, 41, 43, 56, 37, 38, 50, 52, 33, 44, 37, 72, 47, 20),
        intArrayOf(80, 52, 38, 44, 39, 49, 50, 56, 62, 42, 54, 59, 35, 35, 32, 31, 37, 43, 48, 47, 38, 71, 56, 53),
        intArrayOf(51, 25, 36, 54, 47, 71, 53, 59, 41, 42, 57, 50, 38, 31, 27, 33, 26, 40, 42, 31, 25),
        intArrayOf(26, 47, 26, 37, 42, 15, 60, 40, 43, 48, 30, 25, 52, 28, 41, 40, 34, 28, 40, 38, 40, 30, 35, 27, 27, 32, 44, 31),
        intArrayOf(32, 29, 31, 25, 21, 23, 25, 39, 33, 21, 36, 21, 14, 23, 33, 27),
        intArrayOf(31, 16, 23, 21, 13, 20, 40, 13, 27, 33, 34, 31, 13, 40, 58, 24),
        intArrayOf(24, 17, 18, 18, 21, 18, 16, 24, 15, 18, 33, 21, 13),
        intArrayOf(24, 21, 29, 31, 26, 18),
        intArrayOf(23, 22, 21, 32, 33, 24),
        intArrayOf(30, 30, 21, 23),
        intArrayOf(29, 23, 25, 18),
        intArrayOf(10, 20, 13, 18, 28),
        intArrayOf(12, 17, 18),
        intArrayOf(20, 15, 16, 16, 25, 21),
        intArrayOf(18, 26, 17, 22),
        intArrayOf(16, 15, 15),
        intArrayOf(25),
        intArrayOf(14, 18, 19, 16, 14, 20, 28, 13, 28, 39, 40, 29, 25),
        intArrayOf(27, 26, 18, 17, 20),
        intArrayOf(25, 25, 22, 19, 14),
        intArrayOf(21, 22, 18),
        intArrayOf(10, 29, 24, 21, 21),
        intArrayOf(13),
        intArrayOf(15),
        intArrayOf(25),
        intArrayOf(20, 29, 22, 11, 14, 17, 17, 13, 21, 11, 19, 17, 18, 20, 8, 21, 18, 24, 21, 15, 27, 21)
)

fun isSingleChapterBook(book: Int): Boolean {
    return numChaptersInBook(book) == 1
}

fun numChaptersInBook(book: Int): Int {
    return verses[book].size
}

fun numVersesInChapter(book: Int, chapter: Int): Int {
    return verses[book][chapter]
}

fun getBookTitle(bookNum: Int): String {
    return AppConstants.bookTitles[bookNum]
}



// UI Utils

fun dpToPx(context: Context, dp: Int): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun pxToDp(context: Context, px: Int): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}