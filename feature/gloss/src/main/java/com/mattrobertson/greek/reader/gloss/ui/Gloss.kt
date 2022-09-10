package com.mattrobertson.greek.reader.gloss.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.db.api.repo.GlossesRepo
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.ui.settings.getComposeFontFamily
import com.mattrobertson.greek.reader.verseref.Word
import kotlinx.coroutines.runBlocking

@Composable
fun Gloss(
    word: Word,
    glossesRepo: GlossesRepo,
    settings: Settings
) {
    val gloss = runBlocking {
        glossesRepo.getGloss(word.lexicalForm)
    }

    Text(
        modifier = Modifier.padding(16.dp),
        text = buildAnnotatedString {
            withStyle(
                style = ParagraphStyle(lineHeight = settings.fontSize * 1.2)
            ) {
                withStyle(
                    style = SpanStyle(
                        fontSize = settings.fontSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = settings.font.getComposeFontFamily()
                    )
                ) {
                    append("${word.lexicalForm}\n")
                }


                withStyle(
                    style = SpanStyle(
                        fontSize = settings.fontSize * 0.8,
                        fontFamily = FontFamily.SansSerif
                    )
                ) {
                    append("${gloss}\n")
                }

                withStyle(
                    style = SpanStyle(
                        fontSize = settings.fontSize * 0.8,
                        fontFamily = FontFamily.SansSerif,
                        fontStyle = FontStyle.Italic
                    )
                ) {
                    append(word.parsing.humanReadable)
                }
            }
        }
    )
}