package com.mattrobertson.greek.reader.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.concordance.ui.Concordance
import com.mattrobertson.greek.reader.db.repo.ConcordanceRepo
import com.mattrobertson.greek.reader.db.repo.GlossesRepo
import com.mattrobertson.greek.reader.db.repo.VerseRepo
import com.mattrobertson.greek.reader.gloss.ui.Gloss
import com.mattrobertson.greek.reader.ui.lib.DragHandle
import com.mattrobertson.greek.reader.verseref.Word

@Composable
fun LexBottomSheetContent(
    word: Word,
    verseRepo: VerseRepo,
    glossesRepo: GlossesRepo,
    concordanceRepo: ConcordanceRepo
) {
    Column(
        modifier = Modifier.padding(top = 12.dp)
    ) {

        DragHandle()

        Gloss(word, glossesRepo)

        Divider()

        Concordance(word, verseRepo, concordanceRepo)
    }
}