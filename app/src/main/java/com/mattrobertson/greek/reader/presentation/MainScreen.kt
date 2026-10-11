package com.mattrobertson.greek.reader.presentation

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mattrobertson.greek.reader.audio.ui.AudioPanel
import com.mattrobertson.greek.reader.reading.ui.ComposeReader
import com.mattrobertson.greek.reader.reading.ui.PaginatedPlanContent
import com.mattrobertson.greek.reader.reading.ui.PlanReaderChapter
import com.mattrobertson.greek.reader.reading.ui.BookTitle
import com.mattrobertson.greek.reader.reading.ui.ChapterText
import com.mattrobertson.greek.reader.reading.ui.TableOfContents
import com.mattrobertson.greek.reader.plans.ui.ReadingPlansScreen
import com.mattrobertson.greek.reader.plans.ReadingPlans
import com.mattrobertson.greek.reader.plans.ui.ReadingPlanReminderScheduler
import com.mattrobertson.greek.reader.settings.ui.SettingsScreen
import com.mattrobertson.greek.reader.tutorial.TutorialScreen
import com.mattrobertson.greek.reader.ui.lib.MaxWidthColumn
import com.mattrobertson.greek.reader.ui.settings.scrollLocationDataStore
import com.mattrobertson.greek.reader.ui.theme.AppTheme
import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.verseref.Word
import com.mattrobertson.greek.reader.verseref.Verse
import com.mattrobertson.greek.reader.verseref.getBookTitle
import com.mattrobertson.greek.reader.vocab.ui.VocabScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    initialPlanIndex: Int? = null
) {
    val navController = rememberNavController()
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val context = LocalContext.current
    val appPreferences = remember(context) {
        context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
    }
    var paginatedReader by remember(appPreferences) {
        mutableStateOf(appPreferences.getBoolean("reader_paginated", false))
    }

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
    var planDay by remember {
        mutableStateOf(initialPlanIndex?.let { index ->
            val progress = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE).getInt("plan-$index-day", -1)
            if (index in ReadingPlans.all.indices && progress in ReadingPlans.all[index].days.indices) index to progress else null
        })
    }

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

                                Divider()

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
                                                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.88f)
                                                )
                                            },
                                            selected = currentDestination?.hierarchy?.any { it.route == bottomNavItem.route } == true,
                                            onClick = {
                                                when (bottomNavItem) {
                                                    BottomNavItem.Contents -> {
                                                        screen = Screen.Contents
                                                    }
                                                    BottomNavItem.Plans -> {
                                                        screen = Screen.Plans
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
                    ) { contentPadding ->
                        Box(modifier = Modifier.padding(contentPadding)) {
                            if (planDay == null) ComposeReader(
                                settings = settings,
                                verseRepo = viewModel.verseRepo,
                                listState = listState,
                                paginated = paginatedReader,
                                onWordSelected = {
                                    word = it
                                    coroutineScope.launch {
                                        bottomSheetState.show()
                                    }
                                }
                            ) else PlanDayReader(
                                planIndex = planDay!!.first,
                                dayIndex = planDay!!.second,
                                settings = settings,
                                paginated = paginatedReader,
                                verseRepo = viewModel.verseRepo,
                                onWordSelected = {
                                    word = it
                                    coroutineScope.launch {
                                        bottomSheetState.show()
                                    }
                                },
                                onDismiss = { planDay = null },
                                onComplete = {
                                    val (index, day) = planDay!!
                                    context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
                                        .edit().putInt("plan-$index-day", day + 1).apply()
                                    if (day == ReadingPlans.all[index].days.lastIndex) ReadingPlanReminderScheduler.cancel(context, index)
                                }
                            )
                        }
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
                    visible = (screen == Screen.Plans),
                    enter = slideInVertically(initialOffsetY = { height -> height }),
                    exit = slideOutVertically(targetOffsetY = { height -> height })
                ) {
                    ReadingPlansScreen(
                        onReadChapter = { ref ->
                            coroutineScope.launch {
                                listState.scrollToItem(ref.absoluteChapterNum())
                            }
                            viewModel.onChangeVerseRef(ref)
                            screen = Screen.Reader
                        },
                        onReadToday = { index, day -> planDay = index to day; screen = Screen.Reader },
                        onDismiss = { screen = Screen.Reader }
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
                        paginatedReader = paginatedReader,
                        onPaginatedReaderChange = { enabled ->
                            paginatedReader = enabled
                            appPreferences.edit().putBoolean("reader_paginated", enabled).apply()
                        },
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

@Composable
private fun PlanDayReader(
    planIndex: Int,
    dayIndex: Int,
    settings: com.mattrobertson.greek.reader.settings.Settings,
    paginated: Boolean,
    verseRepo: com.mattrobertson.greek.reader.db.api.repo.VerseRepo,
    onWordSelected: (Word) -> Unit,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    val plan = ReadingPlans.all[planIndex]
    val passages = plan.days[dayIndex]
    var completed by remember(planIndex, dayIndex) { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${plan.title} · Day ${dayIndex + 1}", maxLines = 1) },
                navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Rounded.Close, "Close today's reading") } },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        },
        bottomBar = {
            Surface(elevation = 8.dp) {
                Button(
                    onClick = { if (!completed) { completed = true; onComplete() } },
                    enabled = !completed,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                ) { Text(if (completed) "Day completed" else "Mark day complete") }
            }
        }
    ) { padding ->
        if (paginated) {
            val chapters by produceState(
                initialValue = emptyList<PlanReaderChapter>(),
                key1 = passages
            ) {
                value = passages.map { ref ->
                    PlanReaderChapter(ref, verseRepo.getVersesForChapter(ref))
                }
            }
            if (chapters.size == passages.size) {
                PaginatedPlanContent(
                    settings = settings,
                    chapters = chapters,
                    onWordSelected = onWordSelected,
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            } else {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("Today's reading", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                        Text(passages.joinToString { "${it.book.abbrv} ${it.chapter}" }, style = MaterialTheme.typography.body2)
                    }
                }
                items(passages.size) { index ->
                    val ref = passages[index]
                    var verses by remember(ref) { mutableStateOf(emptyList<Verse>()) }
                    LaunchedEffect(ref) { verses = verseRepo.getVersesForChapter(ref) }
                    Column {
                        if (ref.chapter == 1 || index == 0 || passages[index - 1].book != ref.book) {
                            BookTitle(getBookTitle(ref.book), settings)
                        }
                        ChapterText(settings, ref, verses, onWordSelected = onWordSelected)
                        Spacer(Modifier.height(24.dp))
                    }
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
