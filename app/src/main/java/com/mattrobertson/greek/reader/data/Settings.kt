package com.mattrobertson.greek.reader.data

import android.content.Context
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.mattrobertson.greek.reader.util.SingletonHolder

class Settings private constructor(applicationContext: Context) {

    private val prefs = getDefaultSharedPreferences(applicationContext)

    companion object : SingletonHolder<Settings, Context>(::Settings) {
        private const val KEY_RECENTS = "recents"
    }

    private fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    private fun loadString(key: String): String {
        return prefs.getString(key, "") ?: ""
    }

    fun saveRecents() {
        save(KEY_RECENTS, Recents.toJson())
    }

    fun loadRecents() {
        val json = loadString(KEY_RECENTS)
        Recents.fromJson(json)
    }
}