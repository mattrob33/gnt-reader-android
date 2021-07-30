package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mattrobertson.greek.reader.compose.ui.theme.AppTheme
import com.mattrobertson.greek.reader.model.VerseRef
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.repo.VerseRepo
import com.mattrobertson.greek.reader.settings.scrollLocationDataStore
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
                    BottomNavItem.Vocab -> {
                        val ref = VerseRef.fromAbsoluteChapterNum(listState.firstVisibleItemIndex)
                        VocabScreen(ref)
                    }
                    BottomNavItem.Audio -> {}
                    BottomNavItem.Settings -> {}
                    null -> {
                        word?.let { word ->
                            activeBottomNavItem = null
                            LexBottomSheetContent(word, verseRepo)
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
                        activeBottomNavItem = null

                        coroutineScope.launch {
                            bottomSheetState.show()
                        }
                    }
                )
            }
        }
    }
}