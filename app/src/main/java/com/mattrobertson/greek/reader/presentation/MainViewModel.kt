package com.mattrobertson.greek.reader.presentation

import androidx.lifecycle.ViewModel
import com.mattrobertson.greek.reader.db.repo.ConcordanceRepo
import com.mattrobertson.greek.reader.db.repo.GlossesRepo
import com.mattrobertson.greek.reader.db.repo.VerseRepo
import com.mattrobertson.greek.reader.db.repo.VocabRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val verseRepo: VerseRepo,
    val glossesRepo: GlossesRepo,
    val concordanceRepo: ConcordanceRepo,
    val vocabRepo: VocabRepo
): ViewModel() {



}