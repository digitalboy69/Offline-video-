package com.example.ui.components

import com.example.data.local.entity.WatchHistory
import com.example.data.model.LocalVideo

/** 0..1 how far a video was watched, from its last saved position. */
fun watchFraction(history: Map<String, WatchHistory>, video: LocalVideo): Float {
    if (video.durationMs <= 0L) return 0f
    val position = history[video.uri.toString()]?.lastPositionMs ?: 0L
    return (position.toFloat() / video.durationMs.toFloat()).coerceIn(0f, 1f)
}

/** A video counts as watched once it has been played at least once. */
fun isWatched(history: Map<String, WatchHistory>, video: LocalVideo): Boolean =
    (history[video.uri.toString()]?.playCount ?: 0) > 0
