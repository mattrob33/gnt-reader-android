package com.mattrobertson.greek.reader

import android.app.Application
import android.content.Context

class SblGntApplication: Application() {
	companion object {
		lateinit var context: Context
	}

	override fun onCreate() {
		super.onCreate()
		context = applicationContext
	}
}