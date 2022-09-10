package com.mattrobertson.greek.reader.vocab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.db.api.models.GlossModel
import com.mattrobertson.greek.reader.db.api.repo.VocabRepo
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.ui.lib.DialogTopBar
import com.mattrobertson.greek.reader.ui.lib.ScrollableChipRow
import com.mattrobertson.greek.reader.ui.lib.VSpacer
import com.mattrobertson.greek.reader.ui.settings.getComposeFontFamily
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.getBookTitleLocalized
import kotlinx.coroutines.runBlocking

@Composable
fun VocabScreen(
    ref: VerseRef,
    vocabRepo: VocabRepo,
    settings: Settings,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        var words by remember { mutableStateOf(emptyList<GlossModel>()) }

        var maxOcc by remember { mutableStateOf(100) }

        words = runBlocking {
            vocabRepo.getVocabWordsForChapter(ref, maxOcc)
        }

        VocabScreenInternal(
            ref = ref,
            words = words,
            onDismiss = onDismiss,
            settings = settings,
            onChangeMaxOcc = {
                maxOcc = it
            }
        )
    }
}

@Composable
private fun VocabScreenInternal(
    ref: VerseRef,
    words: List<GlossModel>,
    settings: Settings,
    onDismiss: () -> Unit,
    onChangeMaxOcc: (max: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        DialogTopBar(
            title = "${getBookTitleLocalized(ref.book)} ${ref.chapter}",
            onDismiss = onDismiss
        )

        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                VocabOccChipRow(
                    onOccChanged = {
                        onChangeMaxOcc(it)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            val mappedWords = words.groupBy {
                when (it.occ) {
                    in 101..Int.MAX_VALUE -> ">100"
                    in 51..100 -> "51-100x"
                    in 31..50 -> "31-50x"
                    in 21..30 -> "21-30x"
                    in 16..20 -> "16-20x"
                    in 11..15 -> "11-15x"
                    in 6..10 -> "6-10x"
                    in 2..5 -> "2-5x"
                    1 -> "1x"
                    else -> ""
                }
            }

            mappedWords.keys.forEach { occ ->
                if (occ.isNotBlank()) {
                    val wordsForOcc = mappedWords[occ]

                    if (wordsForOcc?.isNotEmpty() == true) {
                        item {
                            Text(
                                text = occ,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                fontFamily = FontFamily.Serif,
                                fontSize = settings.fontSize * 0.8,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Divider(
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )
                        }

                        wordsForOcc.forEach { word ->
                            item {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(
                                            fontFamily = settings.font.getComposeFontFamily(),
                                            fontSize = settings.fontSize
                                        )) {
                                            append("${word.lex} - ")
                                        }

                                        withStyle(SpanStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontSize = settings.fontSize * 0.8
                                        )) {
                                            append("${word.gloss} (${word.occ}x)")
                                        }
                                    },
                                    color = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }

                        item {
                            VSpacer(40.dp)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun VocabScreenPreview() {
    AppTheme {
        VocabScreenInternal(
            ref = VerseRef(Book.ROMANS, 5),
            words = listOf(
                GlossModel("lex", "gloss", 50),
                GlossModel("lex", "gloss", 48),
                GlossModel("lex", "gloss", 47),
                GlossModel("lex", "gloss", 40),
                GlossModel("lex", "gloss", 25),
                GlossModel("lex", "gloss", 1),
                GlossModel("lex", "gloss", 0),
            ),
            settings = Settings.default,
            onDismiss = {},
            onChangeMaxOcc = {}
        )
    }
}


@Composable
fun VocabOccChipRow(
    onOccChanged: (count: Int) -> Unit
) {
    val chips = listOf(100, 50, 30, 20, 15, 10, 5, 1)

    ScrollableChipRow(
        items = chips.map { "${it}x" },
        backgroundColor = MaterialTheme.colors.primary,
        outlineColor = MaterialTheme.colors.primary,
        textColor = MaterialTheme.colors.primary,
        onItemSelected = { index ->
            val occ = chips[index]
            onOccChanged(occ)
        }
    )
}