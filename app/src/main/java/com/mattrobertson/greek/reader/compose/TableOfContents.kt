package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.util.AppConstants
import com.mattrobertson.greek.reader.util.getAbsoluteChapterNumForBook
import com.mattrobertson.greek.reader.util.verses

@Composable
fun TableOfContents(
    onSelected: (index: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var isBookScreen by remember { mutableStateOf(true) }
    var book by remember { mutableStateOf(Book.MATTHEW) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isBookScreen)
                        onDismiss()
                    else
                        isBookScreen = true
                }
            ) {
                if (isBookScreen)
                    Icon(Icons.Rounded.Close, "Close")
                else
                    Icon(Icons.Rounded.ArrowBack, "Back")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isBookScreen) "" else book.title
            )
        }

        Divider()

        if (isBookScreen) {
            TableOfContentsBooks(
                onBookSelected = { selectedBook ->
                    book = selectedBook
                    isBookScreen = false
                }
            )
        } else {
            TableOfContentsChapters(
                book = book,
                onChapterSelected = { selectedChapter ->
                    val absChapter = getAbsoluteChapterNumForBook(book) + selectedChapter
                    onSelected(absChapter)
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
    LazyColumn {
        itemsIndexed(AppConstants.bookTitles) { index, title ->
            Text(
                text = title,
                modifier = Modifier
                    .clickable {
                        onBookSelected(
                            Book(index)
                        )
                    }
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
                    .fillMaxWidth()
            )
            Divider()
        }
    }
}

@Composable
private fun TableOfContentsChapters(
    book: Book,
    onChapterSelected: (index: Int) -> Unit
) {
    LazyColumn {
        val numChapters = verses[book.num].size

        for (chapterNum in 0 until numChapters) {
            item {
                Text(
                    text = "Chapter ${chapterNum + 1}",
                    modifier = Modifier
                        .clickable {
                            onChapterSelected(chapterNum)
                        }
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp
                        )
                        .fillMaxWidth()
                )
                Divider()
            }
        }
    }
}