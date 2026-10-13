package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.model.FeedFilter
import com.example.ui.theme.GlassStrokeTop
import com.example.ui.theme.TextSecondary

@Composable
fun EmptyFeedView(
    filter: FeedFilter,
    onResetFilter: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFavorites = filter is FeedFilter.Favorites

    val transition = rememberInfiniteTransition(label = "empty")
    val bob = transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "empty_bob"
    )

    AuroraBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_videos),
                contentDescription = "Empty Gallery",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(132.dp)
                    .graphicsLayer { translationY = bob.value }
                    .clip(RoundedCornerShape(32.dp))
                    .border(1.dp, GlassStrokeTop, RoundedCornerShape(32.dp))
                    .staggeredEntrance(0)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isFavorites) "No Favorites Yet" else "No Videos in ${filter.displayName}",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.staggeredEntrance(1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isFavorites) {
                    "Double-tap any video or tap the heart to save it here for quick access."
                } else {
                    "Nothing matches this folder or filter yet. Record or download some videos and they'll appear here."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .staggeredEntrance(2)
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (filter !is FeedFilter.All) {
                GradientButton(
                    text = "View All Videos",
                    icon = Icons.Default.VideoLibrary,
                    onClick = onResetFilter,
                    modifier = Modifier
                        .staggeredEntrance(3)
                        .testTag("empty_view_reset_filter_button")
                )
            } else {
                GradientButton(
                    text = "Refresh Storage Scan",
                    icon = Icons.Default.Refresh,
                    onClick = onRefresh,
                    modifier = Modifier
                        .staggeredEntrance(3)
                        .testTag("empty_view_refresh_button")
                )
            }
        }
    }
}
