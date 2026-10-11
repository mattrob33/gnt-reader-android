package com.mattrobertson.greek.reader.reading.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mattrobertson.greek.reader.db.api.repo.VerseRepo
import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.ui.lib.VSpacer
import com.mattrobertson.greek.reader.ui.lib.sideMargins
import com.mattrobertson.greek.reader.ui.settings.getComposeFontFamily
import com.mattrobertson.greek.reader.ui.settings.scrollLocationDataStore
import com.mattrobertson.greek.reader.verseref.Verse
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.Word
import com.mattrobertson.greek.reader.verseref.getBookTitle
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ComposeReader(
    settings: Settings,
    verseRepo: VerseRepo,
    listState: LazyListState,
    paginated: Boolean = false,
    onWordSelected: (word: Word) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    if (paginated) {
        PaginatedReader(
            settings = settings,
            verseRepo = verseRepo,
            initialChapter = listState.firstVisibleItemIndex,
            onChapterChanged = { chapter ->
                coroutineScope.launch {
                    listState.scrollToItem(chapter)
                    context.scrollLocationDataStore.updateData { scrollLocation ->
                        scrollLocation.toBuilder().setPosition(chapter).setOffset(0).build()
                    }
                }
            },
            onWordSelected = onWordSelected
        )
    } else {
        ScrollingReader(settings, verseRepo, listState, onWordSelected)
    }
}

@Composable
private fun ScrollingReader(
    settings: Settings,
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
                        BookTitle(title = getBookTitle(chapterRef.book), settings)
                    }

                    var verses by remember { mutableStateOf(emptyList<Verse>()) }

                    LaunchedEffect(key1 = chapterRef, block = {
                        coroutineScope.launch {
                            verses = verseRepo.getVersesForChapter(chapterRef)
                        }
                    })

                    ChapterText(
                        settings = settings,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PaginatedReader(
    settings: Settings,
    verseRepo: VerseRepo,
    initialChapter: Int,
    onChapterChanged: (Int) -> Unit,
    onWordSelected: (word: Word) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var chapterIndex by rememberSaveable { mutableIntStateOf(initialChapter.coerceIn(0, 259)) }
    fun moveToChapter(chapter: Int) {
        chapterIndex = chapter.coerceIn(0, 259)
        onChapterChanged(chapterIndex)
    }
    var clickedOffset by remember(chapterIndex) { mutableIntStateOf(-1) }
    var verses by remember(chapterIndex) { mutableStateOf(emptyList<Verse>()) }
    val chapterRef = remember(chapterIndex) { VerseRef.fromAbsoluteChapterNum(chapterIndex) }

    LaunchedEffect(chapterRef) {
        verses = verseRepo.getVersesForChapter(chapterRef)
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (verses.isEmpty()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
            return@BoxWithConstraints
        }

        val wordMap = remember(chapterRef, verses) { mutableMapOf<Int, Word>() }
        val chapterText = buildChapterText(settings, chapterRef, verses, wordMap, clickedOffset)
        val textStyle = TextStyle(
            color = MaterialTheme.colors.onSurface,
            fontFamily = settings.font.getComposeFontFamily(),
            fontSize = settings.fontSize,
            lineHeight = settings.fontSize * settings.lineSpacing
        )
        val margins = sideMargins()
        val density = LocalDensity.current
        val contentWidthPx = with(density) {
            (maxWidth - margins * 2).roundToPx().coerceAtLeast(1)
        }
        // Leave room for the chapter controls and page indicator when measuring the page body.
        val pageBodyHeightPx = with(density) {
            (maxHeight - 112.dp).coerceAtLeast(120.dp).roundToPx()
        }
        val textMeasurer = rememberTextMeasurer()
        val fullLayout = remember(chapterText, textStyle, contentWidthPx, textMeasurer) {
            textMeasurer.measure(
                text = chapterText,
                style = textStyle,
                constraints = Constraints(maxWidth = contentWidthPx)
            )
        }
        val pageSlices = remember(fullLayout, chapterText.length, pageBodyHeightPx) {
            splitIntoPages(fullLayout, chapterText.length, pageBodyHeightPx)
        }
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (chapterIndex > 0) moveToChapter(chapterIndex - 1) },
                    enabled = chapterIndex > 0
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Previous chapter")
                }
                Text(
                    text = "${getBookTitle(chapterRef.book)} ${chapterRef.chapter}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1
                )
                IconButton(
                    onClick = { if (chapterIndex < 259) moveToChapter(chapterIndex + 1) },
                    enabled = chapterIndex < 259
                ) {
                    Icon(Icons.Rounded.ArrowForward, contentDescription = "Next chapter")
                }
            }

            key(chapterIndex) {
                val chapterPagerState = rememberPagerState(pageCount = { pageSlices.size })
                HorizontalPager(
                    state = chapterPagerState,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) { pageIndex ->
                    val slice = pageSlices[pageIndex]
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        ClickableText(
                            text = chapterText.subSequence(slice.start, slice.endExclusive),
                            style = textStyle,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = margins),
                            onClick = { localOffset ->
                                val globalOffset = slice.start + localOffset
                                val tappedWord = wordMap.entries.firstOrNull { (wordOffset, word) ->
                                    globalOffset >= wordOffset && globalOffset < wordOffset + word.text.length
                                }?.value
                                if (tappedWord != null) {
                                    clickedOffset = globalOffset
                                    onWordSelected(tappedWord)
                                }
                            }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    if (pageIndex > 0) {
                                        coroutineScope.launch { chapterPagerState.animateScrollToPage(pageIndex - 1) }
                                    } else if (chapterIndex > 0) {
                                        moveToChapter(chapterIndex - 1)
                                    }
                                },
                                enabled = pageIndex > 0 || chapterIndex > 0
                            ) { Text("Previous") }
                            TextButton(
                                onClick = {
                                    if (pageIndex < pageSlices.lastIndex) {
                                        coroutineScope.launch { chapterPagerState.animateScrollToPage(pageIndex + 1) }
                                    } else if (chapterIndex < 259) {
                                        moveToChapter(chapterIndex + 1)
                                    }
                                },
                                enabled = pageIndex < pageSlices.lastIndex || chapterIndex < 259
                            ) { Text("Next") }
                        }
                    }
                }
            }
        }
    }
}

private data class ReaderPageSlice(val start: Int, val endExclusive: Int)

private fun splitIntoPages(
    layout: TextLayoutResult,
    textLength: Int,
    pageHeightPx: Int
): List<ReaderPageSlice> {
    if (textLength == 0) return listOf(ReaderPageSlice(0, 0))

    val pages = mutableListOf<ReaderPageSlice>()
    var firstLine = 0
    var startOffset = 0
    while (firstLine < layout.lineCount) {
        val pageTop = layout.getLineTop(firstLine)
        var lastLine = firstLine
        while (lastLine + 1 < layout.lineCount &&
            layout.getLineBottom(lastLine + 1) - pageTop <= pageHeightPx
        ) {
            lastLine++
        }

        val endOffset = layout.getLineEnd(lastLine).coerceIn(startOffset + 1, textLength)
        pages += ReaderPageSlice(startOffset, endOffset)
        startOffset = endOffset
        firstLine = lastLine + 1
    }
    if (startOffset < textLength) pages += ReaderPageSlice(startOffset, textLength)
    return pages.ifEmpty { listOf(ReaderPageSlice(0, textLength)) }
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
fun BookTitle(
    title: String,
    settings: Settings
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.h1.copy(
                fontSize = settings.fontSize * 1.5
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ChapterText(
    settings: Settings,
    chapterRef: VerseRef,
    verses: List<Verse>,
    onWordSelected: (word: Word) -> Unit
) {
    var chapterText by remember { mutableStateOf(AnnotatedString("")) }

    val wordMap by remember { mutableStateOf(mutableMapOf<Int, Word>()) }

    var clickedIndex by remember { mutableStateOf(-1) }

    if (verses.isEmpty()) {
        // hack to speed up loading - this ensures only the first ChapterText is visible and
        // loaded, rather than trying to load all of them (since the initial height is ~0)
        //TODO find a better way of doing this
        VSpacer(1200.dp)
        return
    }

    chapterText = buildChapterText(
        settings = settings,
        chapterRef = chapterRef,
        verses = verses,
        wordMap = wordMap,
        clickedIndex = clickedIndex
    )

    Column {
        ClickableText(
            text = chapterText,
            style = TextStyle(
                color = MaterialTheme.colors.onSurface,
                fontFamily =  settings.font.getComposeFontFamily(),
                fontSize = settings.fontSize,
                lineHeight = settings.fontSize * settings.lineSpacing
            ),
            modifier = Modifier.padding(horizontal = sideMargins()),
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

@Composable private fun buildChapterText(
    settings: Settings,
    chapterRef: VerseRef,
    verses: List<Verse>,
    wordMap: MutableMap<Int, Word>,
    clickedIndex: Int
): AnnotatedString {

    var offset = 0

    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(fontSize = settings.fontSize * 1.5)
        ) {
            val text = " ${chapterRef.chapter} "
            offset += text.length
            append(text)
        }

        verses.forEach {
            if (settings.showVerseNumbers) {
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

                val wordFontTextDecoration =
                    if (isClicked)
                        TextDecoration.Underline
                    else
                        TextDecoration.None

                withStyle(
                    style = SpanStyle(
                        fontWeight = wordFontWeight,
                        textDecoration = wordFontTextDecoration
                    )
                ) {
                    append(word.text)
                }
                append(" ")
            }

            if (settings.versesOnNewLines) {
                append("\n")
            }
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
