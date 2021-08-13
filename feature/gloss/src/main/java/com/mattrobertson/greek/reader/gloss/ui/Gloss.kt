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
import com.mattrobertson.greek.reader.ui.theme.SblGreek
import com.mattrobertson.greek.reader.verseref.Word
import kotlinx.coroutines.runBlocking

@Composable
fun Gloss(
    word: Word,
    glossesRepo: GlossesRepo
) {
    val gloss = runBlocking {
        glossesRepo.getGloss(word.lexicalForm)
    }

    Text(
        modifier = Modifier.padding(16.dp),
        text = buildAnnotatedString {
            withStyle(
                style = ParagraphStyle(lineHeight = 28.sp)
            ) {
                withStyle(
                    style = SpanStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SblGreek
                    )
                ) {
                    append("${word.lexicalForm}\n")
                }


                withStyle(
                    style = SpanStyle(
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                ) {
                    append("${gloss}\n")
                }

                withStyle(
                    style = SpanStyle(
                        fontSize = 18.sp,
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