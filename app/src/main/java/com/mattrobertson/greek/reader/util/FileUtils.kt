package com.mattrobertson.greek.reader.util

import android.content.res.AssetManager
import java.io.InputStream
import java.util.*

fun getFileName(forBook: Int): String {
    val name = AppConstants.fNames[forBook]
    return "$name.txt"
}

fun readEntireFileFromAssets(assets: AssetManager, filename: String): String {
    try {
        val inStream: InputStream = assets.open(filename)
        val s = Scanner(inStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    } catch (e: Exception) {
        // TODO : handle exception
    }
    return ""
}