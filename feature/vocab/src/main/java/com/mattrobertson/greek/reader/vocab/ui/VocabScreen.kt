package com.mattrobertson.greek.reader.vocab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.db.VerseDatabase
import com.mattrobertson.greek.reader.db.models.GlossEntity
import com.mattrobertson.greek.reader.ui.lib.ScrollableChipRow
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.getBookAbbrv
import com.mattrobertson.greek.reader.vocab.previews.FakeVerseRefProvider
import kotlinx.coroutines.runBlocking

@Composable
@Preview(name = "Vocab")
fun VocabScreen(
    @PreviewParameter(FakeVerseRefProvider::class)
    ref: VerseRef
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var words by remember { mutableStateOf(emptyList<GlossEntity>()) }

        var maxOcc by remember { mutableStateOf(100) }

        val vocabDao = VerseDatabase.getInstance(LocalContext.current).vocabDao()

        words = runBlocking {
            vocabDao.getVocabWordsForChapter(ref, maxOcc)
        }

        Text(
            text = "Vocabulary for ${getBookAbbrv(ref.book)} ${ref.chapter}",
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        VocabOccChipRow(
            onOccChanged = {
                maxOcc = it
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider()

        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(words) { word ->
                Text(text = "${word.lex} - ${word.gloss} (${word.occ}x)")
            }
        }
    }
}

@Composable
fun VocabOccChipRow(
    onOccChanged: (count: Int) -> Unit
) {
    val chips = listOf("100", "50", "30", "20", "15", "10", "5", "2", "1")

    ScrollableChipRow(
        items = chips,
        backgroundColor = MaterialTheme.colors.primary,
        outlineColor = MaterialTheme.colors.primaryVariant,
        textColor = MaterialTheme.colors.onPrimary,
        onItemSelected = { index ->
            val occ = chips[index].toInt()
            onOccChanged(occ)
        }
    )
}