package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.zIndex
import com.mattrobertson.greek.reader.model.Verse
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.repo.VerseRepo
import com.mattrobertson.greek.reader.util.getBookTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ComposeReader(
    verseRepo: VerseRepo,
    listState: LazyListState,
    wordState: MutableState<Word?>
) {
    val coroutineScope = rememberCoroutineScope()

    Box {
        if (listState.isScrollInProgress) {
            val ref = VerseRef.fromAbsoluteChapterNum(listState.firstVisibleItemIndex)
            val title = "${getBookTitle(ref.book)} ${ref.chapter}"
            ReaderPreviewBar(title)
        }

        ReaderText(
            listState = listState,
            wordState = wordState,
            verseRepo = verseRepo,
            coroutineScope = coroutineScope
        )
    }
}

@Composable
fun ReaderPreviewBar(
    title: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f),
        color = MaterialTheme.colors.surface
    ) {
        Column {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            Divider()
        }
    }
}

@Composable
fun ReaderText(
    listState: LazyListState,
    wordState: MutableState<Word?>,
    verseRepo: VerseRepo,
    coroutineScope: CoroutineScope
) {
    LazyColumn(
        state = listState
    ) {
        for (i in 0 until 260) {
            item {
                val chapterRef = VerseRef.fromAbsoluteChapterNum(i)

                if (chapterRef.chapter == 1) {
                    BookSpacer()
                    BookTitle(title = getBookTitle(chapterRef.book))
                }

                ChapterText(
                    chapterRef = chapterRef,
                    wordState = wordState,
                    verseRepo = verseRepo,
                    coroutineScope = coroutineScope
                )

                ChapterSpacer()
            }
        }
    }
}

@Composable
fun BookTitle(title: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.h1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ChapterText(
    chapterRef: VerseRef,
    wordState: MutableState<Word?>,
    verseRepo: VerseRepo,
    coroutineScope: CoroutineScope
) {
    var isLoading by remember { mutableStateOf(true) }

    var verses by remember { mutableStateOf(listOf<Verse>()) }
    var chapterText by remember { mutableStateOf(AnnotatedString("")) }
    var clickedIndex by remember { mutableStateOf(-1) }

    val wordMap by remember { mutableStateOf(mutableMapOf<Int, Word>()) }

    LaunchedEffect(key1 = chapterRef, block = {
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
            ClickableText(
                text = chapterText,
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = { clickOffset ->
                    wordMap.forEach { entry ->
                        val wordOffset = entry.key
                        val word = entry.value

                        clickedIndex = clickOffset

                        if (wordOffset > clickOffset)
                            return@forEach

                        wordState.value = word
                    }
                }
            )
        }
    }
}

@Composable
fun BookSpacer() {
    Spacer(modifier = Modifier.height(60.dp))
}

@Composable
fun ChapterSpacer() {
    Spacer(modifier = Modifier.height(8.dp))
}
