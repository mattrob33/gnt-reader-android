package com.mattrobertson.greek.reader.concordance.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.db.api.repo.ConcordanceRepo
import com.mattrobertson.greek.reader.db.api.repo.VerseRepo
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.ui.settings.getComposeFontFamily
import com.mattrobertson.greek.reader.ui.theme.Fonts.SblGreek
import com.mattrobertson.greek.reader.verseref.Book
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.Word
import com.mattrobertson.greek.reader.verseref.getBookAbbrv
import kotlinx.coroutines.runBlocking

@Composable
fun Concordance(
    word: Word,
    verseRepo: VerseRepo,
    concordanceRepo: ConcordanceRepo,
    settings: Settings
) {
    val concordanceList = runBlocking {
        concordanceRepo.getConcordanceEntries(word.lexicalForm)
    }

    LazyColumn {
        item {
            Spacer(Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = buildAnnotatedString {
                    withStyle(
                        style = ParagraphStyle(lineHeight = 24.sp)
                    ) {
                        withStyle(
                            style = SpanStyle(
                                fontSize = settings.fontSize * 0.9,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Concordance\n")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontSize = settings.fontSize * 0.8,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("(${concordanceList.size}x)")
                        }
                    }
                }
            )

            Spacer(Modifier.height(8.dp))
        }

        itemsIndexed(concordanceList) {index, entity ->
            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
                fontSize = settings.fontSize * 0.9,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                text = buildAnnotatedString {

                    withStyle(
                        style = SpanStyle(
                            fontSize = settings.fontSize * 0.7f,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Default
                        )
                    ) {
                        val bookTitle = getBookAbbrv(Book(entity.book))
                        append("${index + 1}. $bookTitle ${entity.chapter}:${entity.verse}\n")
                    }

                    withStyle(
                        style = SpanStyle(
                            fontSize = settings.fontSize,
                            fontWeight = FontWeight.Normal,
                            fontFamily = settings.font.getComposeFontFamily()
                        )
                    ) {
                        val ref = VerseRef(
                            Book(entity.book),
                            entity.chapter,
                            entity.verse
                        )
                        val words = runBlocking {
                            verseRepo.getVerseWords(ref)
                        }

                        words.forEach {
                            val selected = (it.lexicalForm == word.lexicalForm)

                            val weight =
                                if (selected)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal

                            val decoration =
                                if (selected)
                                    TextDecoration.Underline
                                else
                                    TextDecoration.None

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = weight,
                                    textDecoration = decoration
                                )
                            ) {
                                append(it.text)
                            }

                            append(" ")
                        }
                    }
                }
            )
        }
    }
}