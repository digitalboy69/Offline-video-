package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AuroraCyan
import com.example.ui.theme.AuroraFuchsia
import com.example.ui.theme.AuroraViolet
import com.example.ui.theme.GlassStrokeTop
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    onLoadDemoClips: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "hero")
    val bob = transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "hero_bob"
    )
    val halo = transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "hero_halo"
    )

    AuroraBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Floating hero logo with breathing halo
            Box(
                modifier = Modifier.staggeredEntrance(0),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .graphicsLayer {
                            scaleX = halo.value
                            scaleY = halo.value
                        }
                        .background(
                            Brush.radialGradient(
                                listOf(AuroraViolet.copy(alpha = 0.55f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_offscroll_logo),
                    contentDescription = "OffScroll Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(116.dp)
                        .graphicsLayer { translationY = bob.value }
                        .clip(RoundedCornerShape(32.dp))
                        .border(1.dp, GlassStrokeTop, RoundedCornerShape(32.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "OffScroll",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.staggeredEntrance(1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Turn your saved videos into a smooth, swipeable vertical feed. No internet. No accounts. Just yours.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .staggeredEntrance(2)
            )

            Spacer(modifier = Modifier.height(28.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(3)
            ) {
                PrivacyItem(
                    icon = Icons.Default.WifiOff,
                    tint = AuroraCyan,
                    title = "100% offline & private",
                    description = "Your videos never leave this device."
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrivacyItem(
                    icon = Icons.Default.VideoLibrary,
                    tint = AuroraFuchsia,
                    title = "Finds everything automatically",
                    description = "Camera, WhatsApp, Downloads, Telegram, screen recordings."
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrivacyItem(
                    icon = Icons.Default.Lock,
                    tint = AuroraViolet,
                    title = "Read-only by default",
                    description = "Standard Android permission. Nothing changes unless you delete."
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            GradientButton(
                text = "Grant Video Access",
                icon = Icons.Default.PlayArrow,
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(4)
                    .testTag("grant_permission_button")
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassButton(
                text = "Preview with Demo Reels",
                onClick = onLoadDemoClips,
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(5)
                    .testTag("demo_preview_button")
            )
        }
    }
}

@Composable
private fun PrivacyItem(
    icon: ImageVector,
    tint: Color,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(tint.copy(alpha = 0.16f), RoundedCornerShape(12.dp))
                .border(1.dp, tint.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
