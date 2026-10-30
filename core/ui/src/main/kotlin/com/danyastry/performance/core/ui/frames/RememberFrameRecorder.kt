package com.danyastry.performance.core.ui.frames

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
fun rememberFrameRecorder(): FrameRecorder {
    val refreshRate = LocalView.current.display?.refreshRate ?: 0f
    return remember(refreshRate) { ChoreographerFrameRecorder(FrameGapStats.intervalNanos(refreshRate)) }
}
