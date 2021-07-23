package com.mattrobertson.greek.reader.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mattrobertson.greek.reader.R
import com.mattrobertson.greek.reader.compose.ui.theme.AppTheme
import com.mattrobertson.greek.reader.model.Word
import com.mattrobertson.greek.reader.repo.VerseRepo
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun MainScreen(
    verseRepo: VerseRepo
) {
    val navController = rememberNavController()
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val coroutineScope = rememberCoroutineScope()

    val wordState = remember { mutableStateOf<Word?>(null) }

    var word by wordState

    if (bottomSheetState.direction == 1f) {
        word = null
    }

    word?.let {
        if (!bottomSheetState.isVisible) {
            coroutineScope.launch {
                bottomSheetState.show()
            }
        }
    }

    bottomSheetState.currentValue

    AppTheme {
        val bottomNavItems = listOf(
            BottomNavItem.Vocab,
            BottomNavItem.Audio,
            BottomNavItem.Settings
        )

        ModalBottomSheetLayout(
            sheetContent = {
                word?.let { word ->
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.sblgreek, FontWeight.Normal))
                                )
                            ) {
                                append("${word.lexicalForm}\n")
                            }

                            withStyle(
                                style = SpanStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            ) {
                                append(word.parsing.humanReadable)
                            }
                        }
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
                                    coroutineScope.launch {
                                        bottomSheetState.show()
                                    }
                                }
                            )
                        }
                    }
                }
            ) {
                ComposeReader(
                    verseRepo = verseRepo,
                    wordState = wordState
                )
            }
        }
    }
}