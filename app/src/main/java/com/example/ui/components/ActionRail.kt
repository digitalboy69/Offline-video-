package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocalVideo
import com.example.ui.theme.FrostedRose
import com.example.ui.theme.FrostedRoseLight
import com.example.ui.theme.GlassBackgroundElevated
import com.example.ui.theme.GlassStrokeBottom
import com.example.ui.theme.GlassStrokeTop

private val LabelShadow = TextStyle(shadow = Shadow(color = Color.Black, blurRadius = 4f))
private val RailBorder = Brush.verticalGradient(listOf(GlassStrokeTop, GlassStrokeBottom))

@Composable
fun ActionRail(
    video: LocalVideo,
    isMuted: Boolean,
    onLike: () -> Unit,
    onOpenPlaylist: () -> Unit,
    onToggleMute: () -> Unit,
    onShare: () -> Unit,
    onOpenMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = 12.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FavoriteActionButton(
            isFavorite = video.isFavorite,
            onClick = onLike
        )

        ActionRailItem(
            icon = Icons.Default.PlaylistAdd,
            label = "Playlist",
            tint = Color(0xFFC4B5FD),
            testTag = "action_playlist_button",
            onClick = onOpenPlaylist
        )

        ActionRailItem(
            icon = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
            label = if (isMuted) "Muted" else "Sound",
            tint = if (isMuted) Color(0xFFFBBF24) else Color.White,
            testTag = "action_mute_button",
            onClick = onToggleMute
        )

        ActionRailItem(
            icon = Icons.Default.Share,
            label = "Share",
            testTag = "action_share_button",
            onClick = onShare
        )

        ActionRailItem(
            icon = Icons.Default.MoreVert,
            label = "More",
            testTag = "action_more_button",
            onClick = onOpenMore
        )
    }
}

@Composable
private fun FavoriteActionButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val confirm = rememberConfirmHaptic()

    val heartScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heart_scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isFavorite) FrostedRose else Color.White,
        label = "heart_color"
    )

    // Expanding ring that fires the moment a video becomes liked
    val burst = remember { Animatable(1f) }
    var wasFavorite by remember { mutableStateOf(isFavorite) }
    LaunchedEffect(isFavorite) {
        if (isFavorite && !wasFavorite) {
            burst.snapTo(0f)
            burst.animateTo(1f, tween(durationMillis = 650, easing = FastOutSlowInEasing))
        }
        wasFavorite = isFavorite
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag("action_favorite_button")
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .graphicsLayer {
                        val s = 1f + burst.value * 0.9f
                        scaleX = s
                        scaleY = s
                        alpha = (1f - burst.value) * 0.85f
                    }
                    .border(2.dp, FrostedRose, CircleShape)
            )
            Box(
                modifier = Modifier
                    .bounceClick(pressedScale = 0.88f) {
                        confirm()
                        onClick()
                    }
                    .size(50.dp)
                    .shadow(10.dp, CircleShape)
                    .background(
                        if (isFavorite) Color(0x40FB7185) else GlassBackgroundElevated,
                        CircleShape
                    )
                    .border(
                        1.dp,
                        if (isFavorite) SolidColor(FrostedRose.copy(alpha = 0.7f)) else RailBorder,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Liked" else "Like video",
                    tint = iconColor,
                    modifier = Modifier
                        .size(25.dp)
                        .graphicsLayer {
                            scaleX = heartScale
                            scaleY = heartScale
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isFavorite) "Liked" else "Like",
            color = if (isFavorite) FrostedRoseLight else Color(0xCCFFFFFF),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            style = LabelShadow
        )
    }
}

@Composable
private fun ActionRailItem(
    icon: ImageVector,
    label: String,
    testTag: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .bounceClick(pressedScale = 0.88f, onClick = onClick)
                .size(50.dp)
                .shadow(8.dp, CircleShape)
                .background(GlassBackgroundElevated, CircleShape)
                .border(1.dp, RailBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(23.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color(0xCCFFFFFF),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            style = LabelShadow
        )
    }
}
