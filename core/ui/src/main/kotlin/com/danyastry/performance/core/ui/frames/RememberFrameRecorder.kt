package com.danyastry.performance.core.ui.frames

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberFrameRecorder(): FrameRecorder {
    val interval = rememberFrameIntervalNanos()
    return remember(interval) { ChoreographerFrameRecorder(interval) }
}
