package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.LocalVideo
import com.example.ui.model.FeedFilter
import com.example.ui.theme.AuroraFuchsia
import com.example.ui.theme.AuroraRose
import com.example.ui.theme.AuroraViolet
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassStrokeBottom
import com.example.ui.theme.GlassStrokeTop
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.VideoFeedUiState
import com.example.ui.viewmodel.VideoFeedViewModel
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ArrowBack
import com.example.ui.theme.GlassBorderSubtle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SearchScreen(
    uiState: VideoFeedUiState,
    viewModel: VideoFeedViewModel,
    onNavigateToFeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var query by rememberSaveable { mutableStateOf("") }
    var showArchive by rememberSaveable { mutableStateOf(false) }
    val trimmed = query.trim()

    val results = remember(trimmed, uiState.allVideos, uiState.hideWatched, uiState.watchHistoryMap) {
        if (trimmed.isEmpty()) {
            emptyList()
        } else {
            uiState.allVideos.filter { video ->
                val matches = video.displayName.contains(trimmed, ignoreCase = true) ||
                    video.bucketDisplayName.contains(trimmed, ignoreCase = true)
                matches && (!uiState.hideWatched || !isWatched(uiState.watchHistoryMap, video))
            }
        }
    }
    val continueWatching = remember(uiState.allVideos, uiState.watchHistoryMap) {
        uiState.allVideos
            .filter { uiState.watchHistoryMap.containsKey(it.uri.toString()) }
            .sortedByDescending { uiState.watchHistoryMap[it.uri.toString()]?.lastPlayedAt ?: 0L }
            .take(10)
    }
    val archiveVideos = remember(uiState.allVideos, uiState.hideWatched, uiState.watchHistoryMap) {
        if (uiState.hideWatched) uiState.allVideos.filterNot { isWatched(uiState.watchHistoryMap, it) } else uiState.allVideos
    }
    val folderCounts = remember(uiState.allVideos) {
        uiState.allVideos.groupingBy { it.bucketDisplayName }.eachCount()
    }
    val progressOf: (LocalVideo) -> Float = { video -> watchFraction(uiState.watchHistoryMap, video) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .appBackdrop()
            .padding(top = statusBarPadding)
            .testTag("search_screen")
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader(title = "Search", subtitle = "Find any video by name or folder")

            SearchField(query = query, onQueryChange = { query = it })

            if (showArchive) {
                ArchiveView(
                    videos = archiveVideos,
                    onBack = { showArchive = false },
                    onPlay = { video ->
                        viewModel.playVideo(video)
                        onNavigateToFeed()
                    },
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            } else if (trimmed.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 128.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (continueWatching.isNotEmpty()) {
                        item {
                            ContinueWatchingRow(
                                videos = continueWatching,
                                progressOf = progressOf,
                                onPlay = { video ->
                                    viewModel.playVideo(video)
                                    onNavigateToFeed()
                                }
                            )
                        }
                    }
                    item {
                        ArchiveEntryCard(count = archiveVideos.size, onClick = { showArchive = true })
                    }
                    item {
                        FolderSection(
                            folders = uiState.folders,
                            folderCounts = folderCounts,
                            onOpenFolder = { name ->
                                viewModel.selectFilter(FeedFilter.ByFolder(name))
                                onNavigateToFeed()
                            }
                        )
                    }
                }
            } else {
                Text(
                    text = if (results.isEmpty()) "No matches" else "${results.size} results",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                if (results.isEmpty()) {
                    Text(
                        text = "Nothing matches \"$trimmed\" yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    VideoGridView(
                        videos = results,
                        onSelectVideo = { index ->
                            viewModel.playVideo(results[index])
                            onNavigateToFeed()
                        },
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        topPadding = 0.dp,
                        progressOf = progressOf
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, GlassStrokeTop, shape)
            .testTag("search_field"),
        singleLine = true,
        shape = shape,
        placeholder = { Text("Videos or folders", color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search", tint = TextMuted)
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0x1FFFFFFF),
            unfocusedContainerColor = Color(0x14FFFFFF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = AuroraFuchsia,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun FolderSection(
    folders: List<String>,
    folderCounts: Map<String, Int>,
    onOpenFolder: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Folders", style = MaterialTheme.typography.titleLarge, color = Color.White)
        if (folders.isEmpty()) {
            Text("No folders found yet.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(folders, key = { it }) { name ->
                    FolderCard(
                        name = name,
                        count = folderCounts[name] ?: 0,
                        onClick = { onOpenFolder(name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FolderCard(name: String, count: Int, onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .width(156.dp)
            .bounceClick(pressedScale = 0.96f, onClick = onClick)
            .clip(shape)
            .background(Brush.linearGradient(listOf(Color(0x2EFFFFFF), Color(0x0DFFFFFF))))
            .border(1.dp, Brush.linearGradient(listOf(GlassStrokeTop, GlassStrokeBottom)), shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(AuroraViolet, AuroraFuchsia))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Text(
            text = name,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = "$count videos", color = TextMuted, fontSize = 12.sp)
    }
}


@Composable
private fun ContinueWatchingRow(
    videos: List<LocalVideo>,
    progressOf: (LocalVideo) -> Float,
    onPlay: (LocalVideo) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().staggeredEntrance(2)) {
        Text(
            text = "Continue watching",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(videos, key = { it.id }) { video ->
                ContinueCard(
                    video = video,
                    progress = progressOf(video),
                    onClick = { onPlay(video) }
                )
            }
        }
    }
}

@Composable
private fun ContinueCard(
    video: LocalVideo,
    progress: Float,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .width(138.dp)
            .bounceClick(pressedScale = 0.95f, onClick = onClick)
            .testTag("continue_card_${video.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .clip(shape)
                .background(Color(0xFF1E293B))
                .border(1.dp, GlassStrokeTop, shape)
        ) {
            if (!video.isSample) {
                VideoThumbnail(video = video, modifier = Modifier.fillMaxSize())
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF312E81), Color(0xFF4C1D95), Color(0xFF0F172A))
                            )
                        )
                )
            }

            // Soft scrim keeps the play badge readable on bright frames
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0x33000000), Color(0xB3020617))
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(38.dp)
                    .background(Color(0x66000000), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Resume",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Progress track along the bottom edge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color(0x33FFFFFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(Brush.horizontalGradient(listOf(AuroraViolet, AuroraRose)))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = video.displayName,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${(progress * 100).toInt()}% watched",
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}


// ───────────── Archive: videos grouped by month and day, with a draggable date scrubber ─────────────

private sealed class ArchiveItem {
    abstract val key: String
}

private data class ArchiveHeader(val monthLabel: String, override val key: String) : ArchiveItem()

private data class ArchiveRow(val videos: List<LocalVideo>, val dayLabel: String, override val key: String) : ArchiveItem()

private val archiveMonthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
private val archiveDayFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
private val archiveKeyFormat = SimpleDateFormat("yyyy-MM", Locale.US)

private fun buildArchive(videos: List<LocalVideo>): List<ArchiveItem> {
    val out = ArrayList<ArchiveItem>()
    val sorted = videos.sortedByDescending { it.dateAddedSec }
    sorted.groupBy { archiveKeyFormat.format(Date(it.dateAddedSec * 1000L)) }.forEach { (monthKey, monthVideos) ->
        val monthDate = Date(monthVideos.first().dateAddedSec * 1000L)
        out += ArchiveHeader(monthLabel = archiveMonthFormat.format(monthDate), key = "h-$monthKey")
        monthVideos.chunked(3).forEachIndexed { rowIndex, row ->
            val dayDate = Date(row.first().dateAddedSec * 1000L)
            out += ArchiveRow(videos = row, dayLabel = archiveDayFormat.format(dayDate), key = "r-$monthKey-$rowIndex")
        }
    }
    return out
}

private fun archiveIndexFor(fraction: Float, size: Int): Int {
    if (size <= 1) return 0
    return (fraction * (size - 1)).roundToInt().coerceIn(0, size - 1)
}

@Composable
private fun ArchiveEntryCard(count: Int, onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(pressedScale = 0.98f, onClick = onClick)
            .clip(shape)
            .background(Brush.linearGradient(listOf(Color(0x2EFFFFFF), Color(0x0DFFFFFF))))
            .border(1.dp, Brush.linearGradient(listOf(GlassStrokeTop, GlassStrokeBottom)), shape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(AuroraFuchsia, AuroraViolet))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Archive", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text("Browse $count videos by date", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}

@Composable
private fun ArchiveView(
    videos: List<LocalVideo>,
    onBack: () -> Unit,
    onPlay: (LocalVideo) -> Unit,
    modifier: Modifier = Modifier
) {
    val archive = remember(videos) { buildArchive(videos) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var scrubbing by remember { mutableStateOf(false) }
    var scrubFraction by remember { mutableFloatStateOf(0f) }

    // Where the list currently is, as 0..1, so the thumb follows normal scrolling too
    val scrollFraction by remember(archive) {
        derivedStateOf {
            if (archive.size <= 1) 0f else listState.firstVisibleItemIndex.toFloat() / (archive.size - 1)
        }
    }
    val shownFraction = (if (scrubbing) scrubFraction else scrollFraction).coerceIn(0f, 1f)
    val activeIndex = archiveIndexFor(shownFraction, archive.size)
    val activeLabel = when (val item = archive.getOrNull(activeIndex)) {
        is ArchiveHeader -> item.monthLabel
        is ArchiveRow -> item.dayLabel
        null -> ""
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Archive", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text("${videos.size} videos by date", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val trackHeight = maxHeight

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(end = 36.dp),
                contentPadding = PaddingValues(bottom = 128.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(archive, key = { it.key }) { entry ->
                    when (entry) {
                        is ArchiveHeader -> Text(
                            text = entry.monthLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                        is ArchiveRow -> ArchiveRowCells(row = entry.videos, onPlay = onPlay)
                    }
                }
            }

            // Draggable date scrubber on the right edge
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(36.dp)
                    .pointerInput(archive) {
                        detectVerticalDragGestures(
                            onDragStart = { offset ->
                                scrubbing = true
                                scrubFraction = (offset.y / size.height).coerceIn(0f, 1f)
                                scope.launch { listState.scrollToItem(archiveIndexFor(scrubFraction, archive.size)) }
                            },
                            onVerticalDrag = { change, _ ->
                                change.consume()
                                scrubFraction = (change.position.y / size.height).coerceIn(0f, 1f)
                                scope.launch { listState.scrollToItem(archiveIndexFor(scrubFraction, archive.size)) }
                            },
                            onDragEnd = { scrubbing = false },
                            onDragCancel = { scrubbing = false }
                        )
                    }
            ) {
                // Track
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight()
                        .width(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x26FFFFFF))
                )
                // Thumb
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (trackHeight - 14.dp) * shownFraction)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(AuroraViolet, AuroraFuchsia)))
                )
                // Date bubble, shown only while dragging
                if (scrubbing && activeLabel.isNotEmpty()) {
                    Text(
                        text = activeLabel,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-34).dp, y = (trackHeight - 14.dp) * shownFraction - 2.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xE6000000))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchiveRowCells(row: List<LocalVideo>, onPlay: (LocalVideo) -> Unit) {
    val cellShape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        row.forEach { video ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(9f / 16f)
                    .clip(cellShape)
                    .bounceClick(pressedScale = 0.95f) { onPlay(video) }
                    .border(1.dp, GlassBorderSubtle, cellShape)
            ) {
                VideoThumbnail(video = video, modifier = Modifier.fillMaxSize())
            }
        }
        repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
    }
}
