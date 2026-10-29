package com.danyastry.performance.feature.scrolling.jank

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.metrics.performance.FrameDataApi31
import androidx.metrics.performance.JankStats

@Composable
fun JankStatsEffect(active: Boolean, counter: JankCounter) {
    val context = LocalContext.current
    val currentActive by rememberUpdatedState(active)
    DisposableEffect(context, counter) {
        val window = context.findActivity()?.window
        val stats = window?.let {
            JankStats.createAndTrack(it) { frame ->
                if (currentActive) {
                    val overrun = (frame as? FrameDataApi31)?.frameOverrunNanos
                    counter.record(frame.frameDurationUiNanos, isJank = overrun?.let { nanos -> nanos > 0 } ?: frame.isJank)
                }
            }
        }
        onDispose { stats?.isTrackingEnabled = false }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
