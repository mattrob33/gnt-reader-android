package com.mattrobertson.greek.reader.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mattrobertson.greek.reader.reading.ui.ComposeReader
import com.mattrobertson.greek.reader.reading.ui.TableOfContents
import com.mattrobertson.greek.reader.ui.settings.scrollLocationDataStore
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.Word
import com.mattrobertson.greek.reader.vocab.ui.VocabScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
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

    var screen by remember { mutableStateOf(Screen.Reader) }

    var audioControlsVisible by remember { mutableStateOf(false) }

    var word by remember { mutableStateOf<Word?>(null) }

    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ModalBottomSheetLayout(
                sheetContent = {
                    word?.let { word ->
                        LexBottomSheetContent(
                            word,
                            viewModel.verseRepo,
                            viewModel.glossesRepo,
                            viewModel.concordanceRepo
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
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
                                        when (bottomNavItem) {
                                            BottomNavItem.Contents -> {
                                                screen = Screen.Contents
                                            }
                                            BottomNavItem.Vocab -> {
                                                screen = Screen.Vocab
                                            }
                                            BottomNavItem.Audio -> {
                                                audioControlsVisible = true
                                            }
                                            BottomNavItem.Settings -> {
                                                screen = Screen.Settings
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) {
                    ComposeReader(
                        verseRepo = viewModel.verseRepo,
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

            when (screen) {
                Screen.Contents -> {
                    TableOfContents(
                        onSelected = { position ->
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                listState.scrollToItem(position)
                            }
                            screen = Screen.Reader
                        },
                        onDismiss = {
                            screen = Screen.Reader
                        }
                    )
                }

                Screen.Vocab -> {
                    val ref = VerseRef.fromAbsoluteChapterNum(listState.firstVisibleItemIndex)
                    VocabScreen(
                        ref,
                        viewModel.vocabRepo,
                        onDismiss = {
                            screen = Screen.Reader
                        }
                    )
                }

                Screen.Settings -> {}

                else -> {}
            }
        }
    }
}