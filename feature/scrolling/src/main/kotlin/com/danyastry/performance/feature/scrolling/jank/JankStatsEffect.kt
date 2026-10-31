package com.danyastry.performance.feature.scrolling.jank

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.metrics.performance.FrameDataApi31
import androidx.metrics.performance.JankStats
import com.danyastry.performance.core.ui.platform.findActivity

@Composable
fun JankStatsEffect(active: Boolean, counter: JankCounter) {
    val context = LocalContext.current
    val currentActive by rememberUpdatedState(active)
    DisposableEffect(context, counter) {
        val window = context.findActivity()?.window
        val stats = window?.let {
            JankStats.createAndTrack(it) { frame ->
                if (currentActive) {
                    val overrun = if (frame is FrameDataApi31) frame.frameOverrunNanos else JankCounter.NO_OVERRUN
                    counter.record(frame.frameDurationUiNanos, overrun, frame.isJank)
                }
            }
        }
        onDispose { stats?.isTrackingEnabled = false }
    }
    LaunchedEffect(active, counter) {
        if (!active) counter.flush()
    }
}
