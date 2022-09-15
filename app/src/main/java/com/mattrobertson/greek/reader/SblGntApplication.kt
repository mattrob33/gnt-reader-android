package com.mattrobertson.greek.reader

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SblGntApplication: Application() {

	companion object {
		lateinit var context: Context
			private set
	}

	override fun onCreate() {
		super.onCreate()
		context = this
	}

}