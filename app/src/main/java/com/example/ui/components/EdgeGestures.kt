package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioManager
import android.provider.Settings
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuroraFuchsia
import com.example.ui.theme.AuroraViolet
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

enum class EdgeZone { NONE, BRIGHTNESS, VOLUME }

data class EdgeHud(val zone: EdgeZone, val level: Float, val id: Long)

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/** Current brightness 0..1: this window's value when set, otherwise the system value. */
fun currentBrightness(context: Context): Float {
    val windowValue = context.findActivity()?.window?.attributes?.screenBrightness ?: -1f
    if (windowValue >= 0f) return windowValue
    return try {
        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
    } catch (e: Exception) {
        0.5f
    }
}

/** Sets this window's brightness only (other apps are not affected). Returns the applied level. */
fun setBrightness(context: Context, level: Float): Float {
    val clamped = level.coerceIn(0.02f, 1f)
    val activity = context.findActivity() ?: return clamped
    val attrs = activity.window.attributes
    attrs.screenBrightness = clamped
    activity.window.attributes = attrs
    return clamped
}

fun currentVolume(context: Context): Float {
    val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
    return audio.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / max
}

/** Sets media volume to a 0..1 level. Only calls the system when the whole step changes. */
fun setVolume(context: Context, level: Float): Float {
    val clamped = level.coerceIn(0f, 1f)
    val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
    val target = (clamped * max).roundToInt().coerceIn(0, max)
    if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) != target) {
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
    }
    return clamped
}

/**
 * Level bar shown while dragging an edge. It reads the state itself, so only this small
 * overlay redraws on each drag step, not the whole reel.
 */
@Composable
fun BoxScope.EdgeHudOverlay(hudState: MutableState<EdgeHud?>) {
    val hud = hudState.value ?: return
    var visible by remember(hud.id) { mutableStateOf(true) }
    // Every level change keeps the bar visible; it fades about 0.9s after the finger stops
    LaunchedEffect(hud.id, hud.level) {
        visible = true
        delay(900)
        visible = false
    }
    val shownLevel by animateFloatAsState(
        targetValue = hud.level,
        animationSpec = tween(durationMillis = 90),
        label = "edge_hud_level"
    )
    val isBrightness = hud.zone == EdgeZone.BRIGHTNESS
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(120)),
        exit = fadeOut(tween(300)),
        modifier = Modifier
            .align(if (isBrightness) Alignment.CenterStart else Alignment.CenterEnd)
            .padding(horizontal = 18.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isBrightness) Icons.Default.WbSunny else Icons.Default.VolumeUp,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(shownLevel.coerceIn(0f, 1f))
                        .background(Brush.verticalGradient(listOf(AuroraFuchsia, AuroraViolet)))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(shownLevel * 100).roundToInt()}%",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
