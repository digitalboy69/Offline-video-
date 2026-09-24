package com.example.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.PopupPlayerOverlay
import com.example.ui.components.VideoFeedScreen
import com.example.ui.viewmodel.VideoFeedUiState
import com.example.ui.viewmodel.VideoFeedViewModel
import androidx.compose.ui.unit.dp

enum class NavRoute(val title: String, val icon: ImageVector) {
    Feed("Feed", Icons.Default.Home),
    Discover("Discover", Icons.Default.Explore),
    Library("Library", Icons.Default.VideoLibrary),
    Settings("Settings", Icons.Default.Settings)
}

@Composable
fun AppNavigation(
    uiState: VideoFeedUiState,
    viewModel: VideoFeedViewModel,
    onEnterPiP: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoute.Feed.name

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth < 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (isCompact && !uiState.isInPictureInPicture) {
                    NavigationBar(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ) {
                        NavRoute.values().forEach { route ->
                            NavigationBarItem(
                                icon = { Icon(route.icon, contentDescription = route.title) },
                                label = { Text(route.title) },
                                selected = currentRoute == route.name,
                                onClick = {
                                    if (currentRoute != route.name) {
                                        navController.navigate(route.name) {
                                            popUpTo(NavRoute.Feed.name) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                if (!isCompact && !uiState.isInPictureInPicture) {
                    NavigationRail(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ) {
                        NavRoute.values().forEach { route ->
                            NavigationRailItem(
                                icon = { Icon(route.icon, contentDescription = route.title) },
                                label = { Text(route.title) },
                                selected = currentRoute == route.name,
                                onClick = {
                                    if (currentRoute != route.name) {
                                        navController.navigate(route.name) {
                                            popUpTo(NavRoute.Feed.name) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = NavRoute.Feed.name,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(NavRoute.Feed.name) {
                        VideoFeedScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onEnterPiP = onEnterPiP,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(NavRoute.Discover.name) {
                        com.example.ui.components.DiscoverScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onNavigateToFeed = {
                                navController.navigate(NavRoute.Feed.name) {
                                    popUpTo(NavRoute.Feed.name) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(NavRoute.Library.name) {
                        com.example.ui.components.LibraryScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onNavigateToFeed = {
                                navController.navigate(NavRoute.Feed.name) {
                                    popUpTo(NavRoute.Feed.name) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(NavRoute.Settings.name) {
                        com.example.ui.components.SettingsScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Floating In-App Pop-up Player
        if (uiState.isPopupPlayerActive && uiState.popupVideo != null) {
            PopupPlayerOverlay(
                video = uiState.popupVideo,
                isMuted = uiState.isMuted,
                onToggleMute = { viewModel.toggleMute() },
                onExpand = {
                    viewModel.expandPopupPlayer(onNavigateToFeed = {
                        if (currentRoute != NavRoute.Feed.name) {
                            navController.navigate(NavRoute.Feed.name) {
                                popUpTo(NavRoute.Feed.name) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    })
                },
                onEnterPiP = onEnterPiP,
                onClose = { viewModel.closePopupPlayer() }
            )
        }
    }
}
