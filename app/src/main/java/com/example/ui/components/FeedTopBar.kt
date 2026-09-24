package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.FeedFilter
import com.example.ui.model.SortOption
import com.example.ui.theme.FrostedRose
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle

@Composable
fun FeedTopBar(
    currentFilter: FeedFilter,
    selectedSort: SortOption,
    isShuffleMode: Boolean,
    isGridView: Boolean,
    searchQuery: String,
    isSearchOpen: Boolean,
    folders: List<String>,
    totalVideosCount: Int,
    currentIndex: Int,
    totalStorageFormatted: String,
    onSelectFilter: (FeedFilter) -> Unit,
    onSelectSort: (SortOption) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleGridView: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onRefresh: () -> Unit,
    onOpenPopup: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xF0020617), Color(0x99020617), Color.Transparent)
                )
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main Top Bar Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Filter Selector Dropdown
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .testTag("feed_filter_dropdown_button")
                            .shadow(6.dp, CircleShape)
                            .background(GlassBackground, CircleShape)
                            .border(1.dp, GlassBorder, CircleShape)
                            .clickable { isFilterMenuExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = when (currentFilter) {
                                is FeedFilter.All -> Icons.Default.Videocam
                                is FeedFilter.Favorites -> Icons.Default.Favorite
                                is FeedFilter.RecentlyAdded -> Icons.Default.Schedule
                                is FeedFilter.RecentlyPlayed -> Icons.Default.History
                                is FeedFilter.ByFolder -> Icons.Default.Folder
                                else -> Icons.Default.Videocam
                            },
                            contentDescription = null,
                            tint = when (currentFilter) {
                                is FeedFilter.Favorites -> FrostedRose
                                else -> Color(0xFFC4B5FD)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentFilter.displayName,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "▼",
                            color = Color(0x99FFFFFF),
                            fontSize = 9.sp
                        )
                    }

                    // Filter Dropdown Menu
                    DropdownMenu(
                        expanded = isFilterMenuExpanded,
                        onDismissRequest = { isFilterMenuExpanded = false },
                        modifier = Modifier
                            .background(Color(0xF00F172A))
                            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                            .testTag("filter_dropdown_menu")
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Videos", color = Color.White, fontWeight = FontWeight.Medium) },
                            leadingIcon = {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFFA78BFA))
                            },
                            onClick = {
                                onSelectFilter(FeedFilter.All)
                                isFilterMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Favorites", color = Color.White, fontWeight = FontWeight.Medium) },
                            leadingIcon = {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = FrostedRose)
                            },
                            onClick = {
                                onSelectFilter(FeedFilter.Favorites)
                                isFilterMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Recently Added", color = Color.White, fontWeight = FontWeight.Medium) },
                            leadingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            onClick = {
                                onSelectFilter(FeedFilter.RecentlyAdded)
                                isFilterMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Recently Played", color = Color.White, fontWeight = FontWeight.Medium) },
                            leadingIcon = {
                                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF34D399))
                            },
                            onClick = {
                                onSelectFilter(FeedFilter.RecentlyPlayed)
                                isFilterMenuExpanded = false
                            }
                        )

                        if (folders.isNotEmpty()) {
                            HorizontalDivider(
                                color = GlassBorderSubtle,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Text(
                                text = "FOLDERS",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                            folders.forEach { folderName ->
                                DropdownMenuItem(
                                    text = { Text(folderName, color = Color(0xFFE2E8F0)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFFFBBF24))
                                    },
                                    onClick = {
                                        onSelectFilter(FeedFilter.ByFolder(folderName))
                                        isFilterMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Right Action Icons Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Random / Shuffle Toggle Button
                    IconButton(
                        onClick = onToggleShuffle,
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(4.dp, CircleShape)
                            .background(
                                if (isShuffleMode) Color(0xCC7C3AED) else GlassBackground,
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (isShuffleMode) Color(0xFFC4B5FD) else GlassBorder,
                                CircleShape
                            )
                            .testTag("feed_shuffle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle / Random Mode",
                            tint = if (isShuffleMode) Color(0xFFEDE9FE) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Sort Menu Button
                    Box {
                        IconButton(
                            onClick = { isSortMenuExpanded = true },
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(4.dp, CircleShape)
                                .background(GlassBackground, CircleShape)
                                .border(1.dp, GlassBorder, CircleShape)
                                .testTag("feed_sort_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort videos",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Sort Dropdown Menu
                        DropdownMenu(
                            expanded = isSortMenuExpanded,
                            onDismissRequest = { isSortMenuExpanded = false },
                            modifier = Modifier
                                .background(Color(0xF00F172A))
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                .testTag("sort_dropdown_menu")
                        ) {
                            Text(
                                text = "SORT BY",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                            SortOption.values().forEach { sortOption ->
                                val isSelected = selectedSort == sortOption && !isShuffleMode
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            sortOption.displayName,
                                            color = if (isSelected) Color(0xFFA78BFA) else Color.White,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onSelectSort(sortOption)
                                        isSortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Search Button
                    IconButton(
                        onClick = onToggleSearch,
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(4.dp, CircleShape)
                            .background(
                                if (isSearchOpen) Color(0xCC7C3AED) else GlassBackground,
                                CircleShape
                            )
                            .border(1.dp, GlassBorder, CircleShape)
                            .testTag("feed_search_button")
                    ) {
                        Icon(
                            imageVector = if (isSearchOpen) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search videos",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Grid View / Reels View Switcher
                    IconButton(
                        onClick = onToggleGridView,
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(4.dp, CircleShape)
                            .background(
                                if (isGridView) Color(0xCC38BDF8) else GlassBackground,
                                CircleShape
                            )
                            .border(1.dp, GlassBorder, CircleShape)
                            .testTag("feed_grid_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewStream else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Reels View" else "Grid View",
                            tint = if (isGridView) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Pop-up Player Button
                    IconButton(
                        onClick = onOpenPopup,
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(4.dp, CircleShape)
                            .background(GlassBackground, CircleShape)
                            .border(1.dp, GlassBorder, CircleShape)
                            .testTag("feed_popup_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureInPictureAlt,
                            contentDescription = "Pop-up Player",
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(4.dp, CircleShape)
                            .background(GlassBackground, CircleShape)
                            .border(1.dp, GlassBorder, CircleShape)
                            .testTag("feed_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh videos",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Expandable Search Bar
            AnimatedVisibility(
                visible = isSearchOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .background(Color(0xCC1E293B), RoundedCornerShape(12.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("video_search_input"),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(Color(0xFFA78BFA)),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search by filename or folder...",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Sub-bar: Offline Status Pill & Storage Summary & Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Offline status pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0x3310B981), CircleShape)
                        .border(1.dp, Color(0x3334D399), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "100% Offline",
                        color = Color(0xFF6EE7B7),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Storage summary & counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (totalVideosCount > 0) {
                        Text(
                            text = "$totalVideosCount videos • $totalStorageFormatted",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .background(GlassBackground, CircleShape)
                                .border(1.dp, GlassBorderSubtle, CircleShape)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )

                        if (!isGridView) {
                            val positionText = "${(currentIndex + 1).coerceAtMost(totalVideosCount)} / $totalVideosCount"
                            Text(
                                text = positionText,
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(GlassBackground, CircleShape)
                                    .border(1.dp, GlassBorderSubtle, CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


