package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.VideoFeedScreen
import com.example.ui.components.bounceClick
import com.example.ui.components.rememberTickHaptic
import com.example.ui.theme.AuroraFuchsia
import com.example.ui.theme.AuroraViolet
import com.example.ui.theme.GlassStrokeBottom
import com.example.ui.theme.GlassStrokeTop
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.VideoFeedUiState
import com.example.ui.viewmodel.VideoFeedViewModel

enum class NavRoute(val title: String, val icon: ImageVector) {
    Feed("Feed", Icons.Default.PlayCircle),
    Search("Search", Icons.Default.Search),
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
    val tick = rememberTickHaptic()

    val navigateTo: (NavRoute) -> Unit = { route ->
        if (currentRoute != route.name) {
            tick()
            navController.navigate(route.name) {
                popUpTo(NavRoute.Feed.name) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    val openFeed: () -> Unit = {
        navController.navigate(NavRoute.Feed.name) {
            popUpTo(NavRoute.Feed.name) { inclusive = false }
            launchSingleTop = true
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth < 600.dp
        val isFeedRoute = currentRoute == NavRoute.Feed.name
        val isNavVisible = if (isFeedRoute) {
            uiState.areControlsVisible && !uiState.isInPictureInPicture
        } else {
            !uiState.isInPictureInPicture
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Wide screens (tablets, landscape): navigation rail on the left
            this@Row.AnimatedVisibility(
                visible = !isCompact && isNavVisible,
                enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            ) {
                NavigationRail(
                    containerColor = Color(0xFF000000),
                    contentColor = Color.White
                ) {
                    NavRoute.values().forEach { route ->
                        NavigationRailItem(
                            icon = { Icon(route.icon, contentDescription = route.title) },
                            label = { Text(route.title) },
                            selected = currentRoute == route.name,
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = AuroraViolet.copy(alpha = 0.35f),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            onClick = { navigateTo(route) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = NavRoute.Feed.name,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        fadeIn(tween(300, delayMillis = 60)) +
                            slideInVertically(tween(380, easing = FastOutSlowInEasing)) { it / 28 }
                    },
                    exitTransition = { fadeOut(tween(160)) },
                    popEnterTransition = {
                        fadeIn(tween(300, delayMillis = 60)) +
                            slideInVertically(tween(380, easing = FastOutSlowInEasing)) { it / 28 }
                    },
                    popExitTransition = { fadeOut(tween(160)) }
                ) {
                    composable(NavRoute.Feed.name) {
                        VideoFeedScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onEnterPiP = onEnterPiP,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(NavRoute.Search.name) {
                        com.example.ui.components.SearchScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onNavigateToFeed = openFeed,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(NavRoute.Library.name) {
                        com.example.ui.components.LibraryScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onNavigateToFeed = openFeed,
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

                // Phones: floating bottom bar
                if (isCompact) {
                    AnimatedVisibility(
                        visible = isNavVisible,
                        enter = slideInVertically(
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                            initialOffsetY = { it }
                        ) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        FloatingNavBar(
                            currentRoute = currentRoute,
                            onSelect = navigateTo
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingNavBar(
    currentRoute: String,
    onSelect: (NavRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val routes = NavRoute.values()
    val selectedIndex = routes.indexOfFirst { it.name == currentRoute }.coerceAtLeast(0)
    val shape = RoundedCornerShape(30.dp)

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .widthIn(max = 460.dp)
            .fillMaxWidth()
            .height(68.dp)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xF21A1A24), Color(0xF2050508))))
            .border(1.dp, Brush.verticalGradient(listOf(GlassStrokeTop, GlassStrokeBottom)), shape)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        routes.forEachIndexed { index, route ->
            NavPillItem(
                route = route,
                selected = index == selectedIndex,
                onClick = { onSelect(route) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavPillItem(
    route: NavRoute,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint by animateColorAsState(
        targetValue = if (selected) Color.White else TextMuted,
        animationSpec = tween(220),
        label = "nav_tint"
    )
    val glow by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(280),
        label = "nav_glow"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .bounceClick(pressedScale = 0.9f, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // The highlight sits behind the icon only, so the icon itself never fades
        Box(
            modifier = Modifier.width(60.dp).height(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = glow }
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AuroraViolet.copy(alpha = 0.7f), AuroraFuchsia.copy(alpha = 0.6f))
                        )
                    )
            )
            Icon(
                imageVector = route.icon,
                contentDescription = route.title,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = route.title,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
