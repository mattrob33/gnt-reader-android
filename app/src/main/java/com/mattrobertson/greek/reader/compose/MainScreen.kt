package com.mattrobertson.greek.reader.compose

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mattrobertson.greek.reader.compose.ui.theme.AppTheme

@Composable
@Preview
fun MainScreen() {
    AppTheme {
        val bottomNavItems = listOf(
            BottomNavItem.Vocab,
            BottomNavItem.Audio,
            BottomNavItem.Settings
        )

        val navController = rememberNavController()
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
//                                navController.navigate(screen.route) {
//                                    // Pop up to the start destination of the graph to
//                                    // avoid building up a large stack of destinations
//                                    // on the back stack as users select items
//                                    popUpTo(navController.graph.findStartDestination().id) {
//                                        saveState = true
//                                    }
//                                    // Avoid multiple copies of the same destination when
//                                    // reselecting the same item
//                                    launchSingleTop = true
//                                    // Restore state when reselecting a previously selected item
//                                    restoreState = true
//                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            ComposeReader()
        }
    }
}