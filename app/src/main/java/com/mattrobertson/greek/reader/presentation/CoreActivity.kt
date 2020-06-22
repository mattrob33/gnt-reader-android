package com.mattrobertson.greek.reader.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.presentation.home.HomeFragment

class CoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment.newInstance())
                    .commitNow()
        }
    }
}