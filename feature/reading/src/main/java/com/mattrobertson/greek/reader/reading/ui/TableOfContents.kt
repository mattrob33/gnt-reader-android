package com.mattrobertson.greek.reader.reading.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.ui.lib.DialogTopBar
import com.mattrobertson.greek.reader.ui.lib.noRippleClickable
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import com.mattrobertson.greek.reader.verseref.*

@ExperimentalFoundationApi
@Preview
@Composable
fun TableOfContentsPreview() {
    AppTheme {
        TableOfContents(
            onSelected = {},
            onDismiss = {}
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun TableOfContents(
    onSelected: (index: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var isBookScreen by remember { mutableStateOf(true) }
    var book by remember { mutableStateOf(Book.MATTHEW) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        DialogTopBar(
            title = "Contents",
            onDismiss = {
                onDismiss()
                isBookScreen = true
            }
        )

        if (isBookScreen) {
            TableOfContentsBooks(
                onBookSelected = { selectedBook ->
                    book = selectedBook
                    if (isSingleChapterBook(book)) {
                        val absChapter = getAbsoluteChapterNumForBook(book)
                        onSelected(absChapter)
                    }
                    else {
                        isBookScreen = false
                    }
                }
            )
        } else {
            TableOfContentsChapters(
                book = book,
                onChapterSelected = { selectedChapter ->
                    val absChapter = getAbsoluteChapterNumForBook(book) + selectedChapter
                    onSelected(absChapter)
                    isBookScreen = true
                },
                onBackSelected = {
                    isBookScreen = true
                }
            )
        }
    }
}

@Composable
private fun TableOfContentsBooks(
    onBookSelected: (book: Book) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        itemsIndexed(bookTitles) { index, title ->
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable {
                        onBookSelected(
                            Book(index)
                        )
                    }
                    .padding(
                        vertical = 8.dp
                    )
                    .fillMaxWidth(),
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun TableOfContentsChapters(
    book: Book,
    onChapterSelected: (index: Int) -> Unit,
    onBackSelected: () -> Unit
) {

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .noRippleClickable {
                    onBackSelected()
                }
                .padding(
                    vertical = 24.dp,
                    horizontal = 24.dp
                )
        ) {
            Icon(
                Icons.Rounded.ArrowBackIos,
                "Back to books",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = getBookTitle(book),
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colors.onSurface
            )
        }

        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 80.dp)) {
            val numChapters = verses[book.num].size

            for (chapterNum in 0 until numChapters) {
                item {
                    Text(
                        text = "${chapterNum + 1}",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable {
                                onChapterSelected(chapterNum)
                            }
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            )
                            .fillMaxWidth(),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}