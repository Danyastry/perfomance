package com.danyastry.performance.feature.rendering.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.danyastry.performance.core.ui.platform.findActivity
import com.danyastry.performance.feature.rendering.data.FrameMetricsRecorder
import com.danyastry.performance.feature.rendering.logic.FrameMetricsSink

@Composable
fun FrameMetricsEffect(sink: FrameMetricsSink) {
    val window = LocalContext.current.findActivity()?.window
    DisposableEffect(window, sink) {
        val recorder = FrameMetricsRecorder(sink)
        window?.let(recorder::attach)
        onDispose { recorder.detach() }
    }
}
