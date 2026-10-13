package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AppBgBottom
import com.example.ui.theme.AppBgTop
import com.example.ui.theme.AuroraCyan
import com.example.ui.theme.AuroraFuchsia
import com.example.ui.theme.AuroraRose
import com.example.ui.theme.AuroraViolet
import com.example.ui.theme.GlassFillBottom
import com.example.ui.theme.GlassFillTop
import com.example.ui.theme.GlassStrokeBottom
import com.example.ui.theme.GlassStrokeTop
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────── Backgrounds ───────────────────────────

private fun DrawScope.glow(center: Offset, color: Color, alpha: Float, radius: Float) {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = radius
        )
    )
}

/** Static, zero-cost aurora backdrop for scrolling screens. */
fun Modifier.appBackdrop(): Modifier = this.drawBehind {
    drawRect(brush = Brush.verticalGradient(listOf(AppBgTop, AppBgBottom)))
    glow(Offset(size.width * 0.10f, size.height * 0.02f), AuroraViolet, 0.30f, size.maxDimension * 0.70f)
    glow(Offset(size.width * 0.98f, size.height * 0.40f), AuroraRose, 0.14f, size.maxDimension * 0.55f)
    glow(Offset(size.width * 0.05f, size.height * 0.95f), AuroraCyan, 0.08f, size.maxDimension * 0.50f)
}

/** Slowly drifting aurora for hero screens (splash, onboarding, empty states). */
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val phase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aurora_phase"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBgBottom)
            .drawBehind {
                val a = phase.value * 2f * PI.toFloat()
                val w = size.width
                val h = size.height
                val r = size.maxDimension
                glow(Offset(w * (0.25f + 0.18f * cos(a)), h * (0.18f + 0.08f * sin(a))), AuroraViolet, 0.42f, r * 0.70f)
                glow(Offset(w * (0.80f + 0.12f * sin(a)), h * (0.45f + 0.10f * cos(a))), AuroraFuchsia, 0.22f, r * 0.60f)
                glow(Offset(w * (0.30f + 0.15f * sin(a + 2f)), h * (0.88f + 0.06f * cos(a))), AuroraCyan, 0.16f, r * 0.55f)
            },
        content = content
    )
}

// ─────────────────────────── Interaction ───────────────────────────

/** Springy press-scale + tap callback. Gives every tappable surface the same "alive" feel. */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    pressedScale: Float = 0.94f,
    onClick: () -> Unit
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed = interaction.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (pressed.value) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_scale"
    )
    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

/** Light "tick" haptic for tab switches and toggles. */
@Composable
fun rememberTickHaptic(): () -> Unit {
    val view = LocalView.current
    return remember(view) {
        val action: () -> Unit = { view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK) }
        action
    }
}

/** Slightly stronger haptic for "like" style confirmations. */
@Composable
fun rememberConfirmHaptic(): () -> Unit {
    val view = LocalView.current
    return remember(view) {
        val action: () -> Unit = { view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK) }
        action
    }
}

/** Fade + rise entrance, staggered by [index]. */
fun Modifier.staggeredEntrance(index: Int, stepMs: Int = 70): Modifier = composed {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay((index.coerceIn(0, 10) * stepMs).toLong())
        progress.animateTo(1f, tween(durationMillis = 520, easing = FastOutSlowInEasing))
    }
    this.graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * 36.dp.toPx()
    }
}

// ─────────────────────────── Components ───────────────────────────

/** Frosted glass surface with soft gradient fill and light-catching stroke. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(GlassFillTop, GlassFillBottom)))
            .border(1.dp, Brush.linearGradient(listOf(GlassStrokeTop, GlassStrokeBottom)), shape)
            .padding(contentPadding),
        content = content
    )
}

/** Primary call-to-action: gradient pill with coloured glow and springy press. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    colors: List<Color> = listOf(AuroraViolet, AuroraFuchsia)
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .bounceClick(pressedScale = 0.96f, onClick = onClick)
            .shadow(16.dp, shape, ambientColor = colors.first(), spotColor = colors.first())
            .clip(shape)
            .background(Brush.horizontalGradient(colors))
            .border(1.dp, Color.White.copy(alpha = 0.22f), shape)
            .height(54.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

/** Quiet secondary button — glass outline. */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .bounceClick(pressedScale = 0.97f, onClick = onClick)
            .clip(shape)
            .background(Brush.linearGradient(listOf(GlassFillTop, GlassFillBottom)))
            .border(1.dp, Brush.linearGradient(listOf(GlassStrokeTop, GlassStrokeBottom)), shape)
            .height(50.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, color = Color.White.copy(alpha = 0.92f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Screen title with a small gradient accent bar. */
@Composable
fun ScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.staggeredEntrance(0)) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(AuroraViolet, AuroraRose)))
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
    }
}

/** Soft sweeping skeleton block. */
@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x = transition.animateFloat(
        initialValue = -400f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer_x"
    )
    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x10FFFFFF), Color(0x30FFFFFF), Color(0x10FFFFFF)),
                        start = Offset(x.value, 0f),
                        end = Offset(x.value + 400f, 200f)
                    )
                )
            }
    )
}

/** Branded loading state used while the library is being scanned. */
@Composable
fun FeedLoadingView(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "loading")
    val pulse = transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "loading_pulse"
    )
    AuroraBackground(modifier = modifier) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_offscroll_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer {
                        scaleX = pulse.value
                        scaleY = pulse.value
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, GlassStrokeTop, RoundedCornerShape(24.dp))
            )
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Scanning your videos",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(140.dp)
                    .height(3.dp)
                    .clip(CircleShape),
                color = AuroraFuchsia,
                trackColor = Color(0x22FFFFFF)
            )
        }
    }
}

/** First-launch brand moment. Hosted in MainActivity and faded out after ~1.2s. */
@Composable
fun SplashOverlay(modifier: Modifier = Modifier) {
    val scale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
        launch { logoAlpha.animateTo(1f, tween(450)) }
        delay(250)
        textAlpha.animateTo(1f, tween(500))
    }
    AuroraBackground(modifier = modifier) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_offscroll_logo),
                contentDescription = "OffScroll",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(112.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = logoAlpha.value
                    }
                    .clip(RoundedCornerShape(30.dp))
                    .border(1.dp, GlassStrokeTop, RoundedCornerShape(30.dp))
            )
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "OffScroll",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.graphicsLayer { this.alpha = textAlpha.value }
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your videos. Offline. Endless.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.graphicsLayer { this.alpha = textAlpha.value }
            )
        }
    }
}

// ─────────────────────────── Reveal once ───────────────────────────

/** Keys that have already played their reveal, so recycled grid cells don't re-animate on scroll. */
private val revealedKeys = HashSet<Any>()

/** Soft fade + settle the first time an item with [key] appears. Later re-entries snap in instantly. */
fun Modifier.revealOnce(key: Any): Modifier = composed {
    val alreadyShown = remember(key) { revealedKeys.contains(key) }
    val progress = remember(key) { Animatable(if (alreadyShown) 1f else 0f) }
    LaunchedEffect(key) {
        if (progress.value < 1f) {
            revealedKeys.add(key)
            progress.animateTo(1f, tween(durationMillis = 360, easing = FastOutSlowInEasing))
        }
    }
    this.graphicsLayer {
        alpha = progress.value
        val s = 0.92f + 0.08f * progress.value
        scaleX = s
        scaleY = s
    }
}
