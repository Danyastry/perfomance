package com.danyastry.performance.core.ui.frames

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
fun rememberFrameIntervalNanos(): Long {
    val refreshRate = LocalView.current.display?.refreshRate ?: 0f
    return remember(refreshRate) { FrameGapStats.intervalNanos(refreshRate) }
}

@Composable
fun rememberFrameBudgetMillis(): Double = rememberFrameIntervalNanos() / NANOS_PER_MILLI

private const val NANOS_PER_MILLI = 1_000_000.0
