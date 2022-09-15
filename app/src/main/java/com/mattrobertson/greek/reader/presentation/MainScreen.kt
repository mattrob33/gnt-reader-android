package com.mattrobertson.greek.reader.presentation

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mattrobertson.greek.reader.audio.ui.AudioPanel
import com.mattrobertson.greek.reader.reading.ui.ComposeReader
import com.mattrobertson.greek.reader.reading.ui.TableOfContents
import com.mattrobertson.greek.reader.settings.ui.SettingsScreen
import com.mattrobertson.greek.reader.tutorial.TutorialScreen
import com.mattrobertson.greek.reader.ui.lib.MaxWidthColumn
import com.mattrobertson.greek.reader.ui.settings.scrollLocationDataStore
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.Word
import com.mattrobertson.greek.reader.vocab.ui.VocabScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalAnimationApi
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

    SideEffect {
        val ref = VerseRef.fromAbsoluteChapterNum(listState.firstVisibleItemIndex)
        viewModel.onChangeVerseRef(ref)
    }

    val coroutineScope = rememberCoroutineScope()

    val startingScreen = startingScreen()

    var screen by remember { mutableStateOf(startingScreen) }

    var audioControlsVisible by remember { mutableStateOf(false) }

    var word by remember { mutableStateOf<Word?>(null) }

    val settings by viewModel.settings.collectAsState()

    AppTheme {
        CompositionLocalProvider(LocalElevationOverlay provides null) {
            Box(modifier = Modifier.fillMaxSize()) {
                ModalBottomSheetLayout(
                    sheetContent = {
                        word?.let { word ->
                            LexBottomSheetContent(
                                word,
                                viewModel.verseRepo,
                                viewModel.glossesRepo,
                                viewModel.concordanceRepo,
                                settings
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
                            MaxWidthColumn {

                                Divider(
                                    thickness = 1.5.dp,
                                    color = MaterialTheme.colors.onBackground.copy(
                                        red = MaterialTheme.colors.onBackground.red * 0.5f,
                                        blue = MaterialTheme.colors.onBackground.blue * 0.5f,
                                        green = MaterialTheme.colors.onBackground.green * 0.5f
                                    )
                                )

                                BottomNavigation(
                                    backgroundColor = MaterialTheme.colors.background,
                                    contentColor = MaterialTheme.colors.onBackground,
                                ) {
                                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                                    val currentDestination = navBackStackEntry?.destination
                                    bottomNavItems.forEach { bottomNavItem ->
                                        BottomNavigationItem(
                                            icon = {
                                                Icon(
                                                    bottomNavItem.icon,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colors.onBackground
                                                )
                                            },
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
                                                        audioControlsVisible = !audioControlsVisible
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
                        }
                    ) {
                        ComposeReader(
                            settings = settings,
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

                AnimatedVisibility(
                    visible = audioControlsVisible,
                    enter = slideInVertically(initialOffsetY = { height -> height / 4 }),
                    exit = slideOutVertically(targetOffsetY = { height -> height }),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    AudioPanel(
                        playbackState = viewModel.audioPlaybackState,
                        startingPlaybackSpeedValue = viewModel.audioPlaybackSpeed,
                        startingPronunciationValue = viewModel.audioPronunciation,
                        onDismiss = { audioControlsVisible = false },
                        onTapPlayPause = viewModel::onTapPlayPauseAudio,
                        onTapSkipBack = viewModel::onTapSkipBack,
                        onTapSkipForward = viewModel::onTapSkipForward,
                        onChangePlaybackSpeed = viewModel::setPlaybackSpeed,
                        onChangePronunciation = viewModel::setPronunciation
                    )
                }

                AnimatedVisibility(
                    visible = (screen == Screen.Contents),
                    enter = slideInVertically(initialOffsetY = { height -> height }),
                    exit = slideOutVertically(targetOffsetY = { height -> height })
                ) {
                    TableOfContents(
                        onSelected = { position ->
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                listState.scrollToItem(position)
                            }
                            screen = Screen.Reader

                            val ref = VerseRef.fromAbsoluteChapterNum(position)
                            viewModel.onChangeVerseRef(ref)
                        },
                        onDismiss = {
                            screen = Screen.Reader
                        }
                    )
                }

                AnimatedVisibility(
                    visible = (screen == Screen.Vocab),
                    enter = slideInVertically(initialOffsetY = { height -> height }),
                    exit = slideOutVertically(targetOffsetY = { height -> height })
                ) {
                    val ref = VerseRef.fromAbsoluteChapterNum(listState.firstVisibleItemIndex)
                    VocabScreen(
                        ref,
                        viewModel.vocabRepo,
                        settings = settings,
                        onDismiss = {
                            screen = Screen.Reader
                        }
                    )
                }

                AnimatedVisibility(
                    visible = (screen == Screen.Settings),
                    enter = slideInVertically(initialOffsetY = { height -> height }),
                    exit = slideOutVertically(targetOffsetY = { height -> height })
                ) {
                    SettingsScreen(
                        onBack = {
                            screen = Screen.Reader
                        }
                    )
                }

                if (screen == Screen.Tutorial) {
                    sharedPrefs().edit { putBoolean("has_shown_tutorial", true) }
                    TutorialScreen(onDismiss = { screen = Screen.Reader })
                }
            }
        }
    }
}

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Preview
@Composable fun MainScreen_Preview() {
    AppTheme {
        MainScreen()
    }
}


@Composable private fun startingScreen(): Screen {
    return if (sharedPrefs().getBoolean("has_shown_tutorial", false)) {
        Screen.Reader
    } else {
        Screen.Tutorial
    }
}

@Composable private fun sharedPrefs(): SharedPreferences {
    return LocalContext.current.getSharedPreferences("settings", Context.MODE_PRIVATE)
}