package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuroraCyan
import com.example.ui.theme.AuroraFuchsia
import com.example.ui.theme.AuroraRose
import com.example.ui.theme.AuroraViolet
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.VideoFeedUiState
import com.example.ui.viewmodel.VideoFeedViewModel
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Switch
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun SettingsScreen(
    uiState: VideoFeedUiState,
    viewModel: VideoFeedViewModel,
    modifier: Modifier = Modifier
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .appBackdrop()
            .padding(top = statusBarPadding)
            .testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 680.dp).fillMaxSize().align(Alignment.TopCenter),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ScreenHeader(
                    title = "Settings",
                    subtitle = "Gestures, storage and about"
                )
            }

            item {
                SectionLabel("Player gestures", Modifier.staggeredEntrance(1))
            }
            item {
                GestureHelpCard(
                    icon = Icons.Default.Gesture,
                    title = "Horizontal drag",
                    description = "Drag left or right anywhere on screen to seek with a live preview HUD.",
                    tint = AuroraCyan,
                    modifier = Modifier.staggeredEntrance(2)
                )
            }
            item {
                GestureHelpCard(
                    icon = Icons.Default.Swipe,
                    title = "Vertical swipe",
                    description = "Swipe up or down to jump to the next or previous video.",
                    tint = AuroraFuchsia,
                    modifier = Modifier.staggeredEntrance(3)
                )
            }
            item {
                GestureHelpCard(
                    icon = Icons.Default.TouchApp,
                    title = "Tap & double-tap",
                    description = "Tap to show or hide controls. Double-tap the center to play or pause, and the sides to skip 10 seconds.",
                    tint = AuroraViolet,
                    modifier = Modifier.staggeredEntrance(4)
                )
            }

            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(4),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(icon = Icons.Default.VisibilityOff, tint = AuroraFuchsia)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hide watched videos",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Videos you've played are hidden from the feed and search.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        Switch(
                            checked = uiState.hideWatched,
                            onCheckedChange = { viewModel.toggleHideWatched() }
                        )
                    }
                }
            }
            item {
                SectionLabel("Storage & library", Modifier.staggeredEntrance(5))
            }
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(6),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(icon = Icons.Default.Storage, tint = AuroraCyan)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "${uiState.allVideos.size} videos indexed",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Total size ${uiState.totalStorageFormatted}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassButton(
                            text = "Rescan",
                            icon = Icons.Default.Refresh,
                            onClick = { viewModel.refreshVideos() },
                            modifier = Modifier.weight(1f)
                        )
                        GlassButton(
                            text = "Clear history",
                            icon = Icons.Default.Delete,
                            onClick = { viewModel.clearWatchHistory() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(7),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(icon = Icons.Default.Info, tint = AuroraRose)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "OffScroll",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Version 2.2 Pro • Offline reel engine",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = TextMuted,
        letterSpacing = 1.2.sp,
        modifier = modifier.padding(top = 10.dp)
    )
}

@Composable
private fun IconTile(icon: ImageVector, tint: Color) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .background(tint.copy(alpha = 0.16f), RoundedCornerShape(14.dp))
            .border(1.dp, tint.copy(alpha = 0.32f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun GestureHelpCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    tint: Color = AuroraViolet
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon = icon, tint = tint)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
