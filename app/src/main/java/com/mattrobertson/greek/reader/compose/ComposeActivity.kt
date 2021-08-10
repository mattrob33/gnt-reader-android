package com.mattrobertson.greek.reader.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.mattrobertson.greek.reader.db.repo.VerseRepo
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {

    @Inject lateinit var verseRepo: VerseRepo

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreen(verseRepo)
            }
        }
    }
}