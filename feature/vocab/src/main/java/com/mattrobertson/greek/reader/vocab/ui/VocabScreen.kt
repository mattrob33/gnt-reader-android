package com.mattrobertson.greek.reader.vocab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.db.models.GlossEntity
import com.mattrobertson.greek.reader.db.repo.VocabRepo
import com.mattrobertson.greek.reader.ui.lib.ScrollableChipRow
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.getBookAbbrv
import kotlinx.coroutines.runBlocking

@Composable
fun VocabScreen(
    ref: VerseRef,
    vocabRepo: VocabRepo
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var words by remember { mutableStateOf(emptyList<GlossEntity>()) }

        var maxOcc by remember { mutableStateOf(100) }

        words = runBlocking {
            vocabRepo.getVocabWordsForChapter(ref, maxOcc)
        }

        Text(
            text = "Vocabulary for ${getBookAbbrv(ref.book)} ${ref.chapter}",
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        VocabOccChipRow(
            onOccChanged = {
                maxOcc = it
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider()

        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(words) { word ->
                Text(
                    text = buildAnnotatedString {
                        append("${word.lex} - ")

                        withStyle(SpanStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp
                        )) {
                            append("${word.gloss} (${word.occ}x)")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun VocabOccChipRow(
    onOccChanged: (count: Int) -> Unit
) {
    val chips = listOf(100, 50, 30, 20, 15, 10, 5, 2, 1)

    ScrollableChipRow(
        items = chips.map { "${it}x" },
        backgroundColor = MaterialTheme.colors.primary,
        outlineColor = MaterialTheme.colors.primaryVariant,
        textColor = MaterialTheme.colors.onPrimary,
        onItemSelected = { index ->
            val occ = chips[index]
            onOccChanged(occ)
        }
    )
}