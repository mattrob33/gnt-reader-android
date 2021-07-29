package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.compose.ui.theme.AppTheme
import com.mattrobertson.greek.reader.data.VerseDatabase
import com.mattrobertson.greek.reader.mappers.VerseTextDecoder
import com.mattrobertson.greek.reader.model.Book
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.repo.VerseRepo
import com.mattrobertson.greek.reader.settings.scrollLocationDataStore
import com.mattrobertson.greek.reader.util.getBookAbbrv
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    verseRepo: VerseRepo
) {
    val navController = rememberNavController()
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val context = LocalContext.current

    val scrollLocation = runBlocking { context.scrollLocationDataStore.data.first() }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = scrollLocation.position,
        initialFirstVisibleItemScrollOffset = scrollLocation.offset
    )

    val coroutineScope = rememberCoroutineScope()

    var activeBottomNavItem by remember { mutableStateOf<BottomNavItem?>(null) }

    var word by remember { mutableStateOf<Word?>(null) }

    AppTheme {
        ModalBottomSheetLayout(
            sheetContent = {
                when (activeBottomNavItem) {
                    BottomNavItem.Contents -> {
                        TableOfContents(
                            onSelected = { position ->
                                coroutineScope.launch {
                                    listState.scrollToItem(position)
                                    bottomSheetState.hide()
                                }
                                activeBottomNavItem = null
                            },
                            onDismiss = {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                                activeBottomNavItem = null
                            }
                        )
                    }
                    BottomNavItem.Vocab -> {}
                    BottomNavItem.Audio -> {}
                    BottomNavItem.Settings -> {}
                    null -> {
                        word?.let { word ->
                            activeBottomNavItem = null

                            val glossesDao = VerseDatabase.getInstance(context).glossesDao()

                            val gloss = runBlocking {
                                glossesDao.getGloss(word.lexicalForm)
                            }

                            val concordanceDao = VerseDatabase.getInstance(context).concordanceDao()

                            val concordanceList = runBlocking {
                                concordanceDao.getConcordanceEntries(word.lexicalForm)
                            }

                            Column {
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
                                                    fontFamily = FontFamily(
                                                        Font(
                                                            R.font.sblgreek,
                                                            FontWeight.Normal
                                                        )
                                                    )
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
                                                    fontStyle = Italic
                                                )
                                            ) {
                                                append(word.parsing.humanReadable)
                                            }
                                        }
                                    }
                                )

                                Divider()

                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = ParagraphStyle(lineHeight = 28.sp)
                                        ) {
                                            withStyle(
                                                style = SpanStyle(
                                                    fontSize = 18.sp,
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append("Concordance")
                                            }
                                        }
                                    }
                                )

                                LazyColumn {
                                    itemsIndexed(concordanceList) {index, entity ->
                                        Text(
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 16.dp
                                            ),
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily(
                                                Font(
                                                    R.font.sblgreek,
                                                    FontWeight.Normal
                                                )
                                            ),
                                            text = buildAnnotatedString {

                                                val bookTitle = getBookAbbrv(Book(entity.book))
                                                append("${index + 1}. $bookTitle ${entity.chapter}:${entity.verse}")

                                                withStyle(
                                                    style = ParagraphStyle(
                                                        textIndent = TextIndent(
                                                            firstLine = 16.sp,
                                                            restLine = 16.sp
                                                        )
                                                    )
                                                ) {
                                                    val ref = VerseRef(Book(entity.book), entity.chapter, entity.verse)
                                                    val verse = runBlocking {
                                                        verseRepo.getVerse(ref)
                                                    }

                                                    val text = VerseTextDecoder.decodeAsString(verse.encodedText)

                                                    append(text)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(8.dp),
            scrimColor = Color.Unspecified,
        ) {
            Scaffold(
                bottomBar = {
                    BottomNavigation(
                        backgroundColor = MaterialTheme.colors.surface,
                        contentColor = MaterialTheme.colors.onSurface,
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        bottomNavItems.forEach { bottomNavItem ->
                            BottomNavigationItem(
                                icon = { Icon(bottomNavItem.icon, contentDescription = null) },
                                label = { Text(stringResource(bottomNavItem.label)) },
                                selected = currentDestination?.hierarchy?.any { it.route == bottomNavItem.route } == true,
                                onClick = {
                                    coroutineScope.launch {
                                        activeBottomNavItem = bottomNavItem
                                        bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                    }
                                }
                            )
                        }
                    }
                }
            ) {
                ComposeReader(
                    verseRepo = verseRepo,
                    listState = listState,
                    onWordSelected = {
                        word = it
                        coroutineScope.launch {
                            bottomSheetState.show()
                        }
                    }
                )
            }
        }
    }
}