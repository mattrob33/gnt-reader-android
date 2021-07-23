package com.mattrobertson.greek.reader.compose

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.model.Verse
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.repo.VerseRepo
import com.mattrobertson.greek.reader.util.getBookTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ComposeReader(
    verseRepo: VerseRepo
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        val ref = VerseRef.fromAbsoluteChapterNum(listState.firstVisibleItemIndex)

        val title = "${getBookTitle(ref.book)} ${ref.chapter}"

        ReaderTopBar(title)
        ReaderText(listState, verseRepo = verseRepo, coroutineScope = coroutineScope)
    }
}

@Composable
fun ReaderTopBar(
    title: String
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReaderText(
    listState: LazyListState,
    verseRepo: VerseRepo,
    coroutineScope: CoroutineScope
) {
    LazyColumn(
        state = listState
    ) {
        for (i in 0 until 260) {
            item {
                ChapterText(position = i, verseRepo = verseRepo, coroutineScope = coroutineScope)
            }
        }
    }
}

@Composable
fun ChapterText(
    position: Int,
    verseRepo: VerseRepo,
    coroutineScope: CoroutineScope
) {
    val chapterRef = remember { VerseRef.fromAbsoluteChapterNum(position) }

    var isLoading by remember { mutableStateOf(true) }

    var verses by remember { mutableStateOf(listOf<Verse>()) }
    var chapterText by remember { mutableStateOf(AnnotatedString("")) }
    var clickedIndex by remember { mutableStateOf(-1) }

    val wordMap by remember { mutableStateOf(mutableMapOf<Int, Word>()) }

    LaunchedEffect(key1 = position, block = {
        coroutineScope.launch {
            verses = verseRepo.getVersesForChapter(chapterRef)
            isLoading = false
        }
    })

    var offset = 0

    if (isLoading) {
        // hack to speed up loading - this ensures only the first ChapterText is visible and
        // loaded, rather than trying to load all of them (since the initial height is ~0)
        //TODO find a better way of doing this
        Spacer(
            modifier = Modifier.height(1200.dp)
        )
    }
    else {
        chapterText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(fontSize = 48.sp)
            ) {
                val text = " ${chapterRef.chapter} "
                offset += text.length
                append(text)
            }

            verses.forEach {
                withStyle(
                    style = SpanStyle(
                        fontSize = 16.sp,
                        baselineShift = BaselineShift.Superscript
                    )
                ) {
                    val text = " ${it.verseRef.verse}"
                    offset += text.length
                    append(text)
                }

                it.words.forEach { word ->
                    wordMap[offset] = word
                    val oldOffset = offset
                    offset += word.text.length + 1

                    val isClicked = clickedIndex in oldOffset..offset

                    val wordFontWeight =
                        if (isClicked)
                            FontWeight.Bold
                        else
                            FontWeight.Normal

                    withStyle(style = SpanStyle(fontWeight = wordFontWeight)) {
                        append("${word.text} ")
                    }
                }
            }
        }

        Column {
            val context = LocalContext.current

            ClickableText(
                text = chapterText,
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = { clickOffset ->
                    clickedIndex = clickOffset

                    var clickedWord = ""

                    wordMap.forEach { entry ->
                        val wordOffset = entry.key
                        val word = entry.value

                        if (wordOffset > clickOffset) {
                            return@forEach
                        }
                        clickedWord = "${word.text} (${word.lexicalForm}) ${word.parsing.humanReadable}"
                    }

                    Toast.makeText(context, clickedWord, Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}