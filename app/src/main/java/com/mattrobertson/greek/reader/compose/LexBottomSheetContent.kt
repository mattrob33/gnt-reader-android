package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.concordance.ui.Concordance
import com.mattrobertson.greek.reader.db.repo.VerseRepo
import com.mattrobertson.greek.reader.gloss.ui.Gloss
import com.mattrobertson.greek.reader.ui.lib.DragHandle
import com.mattrobertson.greek.reader.verseref.Word

@Composable
fun LexBottomSheetContent(
    word: Word,
    verseRepo: VerseRepo
) {
    Column(
        modifier = Modifier.padding(top = 12.dp)
    ) {

        DragHandle()

        Gloss(word)

        Divider()

        Concordance(word, verseRepo)
    }
}