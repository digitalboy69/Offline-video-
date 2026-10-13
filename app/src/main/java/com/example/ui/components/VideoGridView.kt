package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocalVideo
import com.example.ui.theme.AuroraRose
import com.example.ui.theme.AuroraViolet
import com.example.ui.theme.GlassBorderSubtle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Thumbnails are scaled down before caching, so 80 of them stay well inside the heap.
private object ThumbnailCache {
    private val cache = LruCache<Long, Bitmap>(80)
    fun get(id: Long): Bitmap? = cache.get(id)
    fun put(id: Long, bitmap: Bitmap) { cache.put(id, bitmap) }
}

private fun loadThumbnail(context: Context, uri: Uri): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, uri)
        val frame = retriever.getFrameAtTime(1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            ?: retriever.getFrameAtTime(0L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        frame?.let { original ->
            val targetHeight = 480
            val targetWidth = (original.width * targetHeight / original.height.coerceAtLeast(1)).coerceAtLeast(1)
            val scaled = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, true)
            if (scaled != original) original.recycle()
            scaled
        }
    } catch (e: Exception) {
        null
    } finally {
        retriever.release()
    }
}

/** Real video frame for a grid cell, loaded off the main thread and cached. */
@Composable
fun VideoThumbnail(video: LocalVideo, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(
        initialValue = ThumbnailCache.get(video.id),
        key1 = video.id,
        key2 = video.uri
    ) {
        if (value == null && !video.isSample) {
            val loaded = withContext(Dispatchers.IO) { loadThumbnail(context, video.uri) }
            if (loaded != null) ThumbnailCache.put(video.id, loaded)
            value = loaded
        }
    }
    val base = if (video.isSample) {
        Brush.linearGradient(listOf(Color(0xFF312E81), Color(0xFF4C1D95), Color(0xFF0F172A)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
    }
    val frame = bitmap
    Box(modifier = modifier.background(base)) {
        if (frame != null && !video.isSample) {
            Image(
                bitmap = frame.asImageBitmap(),
                contentDescription = video.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun VideoGridView(
    videos: List<LocalVideo>,
    onSelectVideo: (Int) -> Unit,
    modifier: Modifier = Modifier,
    topPadding: Dp = 100.dp,
    progressOf: (LocalVideo) -> Float = { 0f }
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.fillMaxSize().testTag("video_grid"),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = topPadding, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(videos, key = { _, video -> video.id }) { index, video ->
            VideoGridCard(
                video = video,
                progress = progressOf(video),
                onClick = { onSelectVideo(index) }
            )
        }
    }
}

@Composable
private fun VideoGridCard(video: LocalVideo, progress: Float, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .revealOnce(video.id)
            .bounceClick(pressedScale = 0.95f, onClick = onClick)
            .clip(shape)
            .border(1.dp, GlassBorderSubtle, shape)
            .testTag("video_grid_card_${video.id}")
    ) {
        VideoThumbnail(video = video, modifier = Modifier.fillMaxSize())

        // Soft bottom scrim keeps the title readable on bright frames
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(72.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xD9000000))))
        )

        // Duration chip
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x99000000))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = video.formattedDuration,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (video.isFavorite) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = Color(0xFFFB7185),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(16.dp)
            )
        }

        Text(
            text = video.displayName,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        )

        // Watch progress along the bottom edge
        if (progress > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color(0x33FFFFFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(Brush.horizontalGradient(listOf(AuroraViolet, AuroraRose)))
                )
            }
        }
    }
}
