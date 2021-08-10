package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
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
import androidx.compose.ui.zIndex
import com.mattrobertson.greek.reader.verseref.Verse
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.Word
import com.mattrobertson.greek.reader.db.repo.VerseRepo
import com.mattrobertson.greek.reader.compose.settings.scrollLocationDataStore
import com.mattrobertson.greek.reader.verseref.getBookTitle
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ComposeReader(
    verseRepo: VerseRepo,
    listState: LazyListState,
    onWordSelected: (word: Word) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    SideEffect {
        coroutineScope.launch {
            context.scrollLocationDataStore.updateData { scrollLocation ->
                scrollLocation.toBuilder()
                    .setPosition(listState.firstVisibleItemIndex)
                    .setOffset(listState.firstVisibleItemScrollOffset)
                    .build()
            }
        }
    }

    Box {
        // Top Preview Bar
        if (listState.isScrollInProgress) {
            val ref = VerseRef.fromAbsoluteChapterNum(listState.firstVisibleItemIndex)
            val title = "${getBookTitle(ref.book)} ${ref.chapter}"
            ReaderPreviewBar(title)
        }

        // Text
        LazyColumn(state = listState) {
            for (i in 0 until 260) {
                item {
                    val chapterRef = VerseRef.fromAbsoluteChapterNum(i)

                    if (chapterRef.chapter == 1) {
                        BookSpacer()
                        BookTitle(title = getBookTitle(chapterRef.book))
                    }

                    var verses by remember { mutableStateOf(emptyList<Verse>()) }

                    LaunchedEffect(key1 = chapterRef, block = {
                        coroutineScope.launch {
                            verses = verseRepo.getVersesForChapter(chapterRef)
                        }
                    })

                    ChapterText(
                        chapterRef = chapterRef,
                        verses = verses,
                        onWordSelected = onWordSelected
                    )

                    ChapterSpacer()
                }
            }
        }
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
    verses: List<Verse>,
    onWordSelected: (word: Word) -> Unit
) {
    var chapterText by remember { mutableStateOf(AnnotatedString("")) }

    val wordMap by remember { mutableStateOf(mutableMapOf<Int, Word>()) }

    var clickedIndex by remember { mutableStateOf(-1) }

    var offset = 0

    if (verses.isEmpty()) {
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

                        onWordSelected(word)
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
