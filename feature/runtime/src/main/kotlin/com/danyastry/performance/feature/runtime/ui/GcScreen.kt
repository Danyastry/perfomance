package com.danyastry.performance.feature.runtime.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danyastry.performance.core.ui.components.SegmentedSelector
import com.danyastry.performance.core.ui.environment.EnvironmentBanner
import com.danyastry.performance.core.ui.frames.FrameGapStats
import com.danyastry.performance.feature.runtime.R
import com.danyastry.performance.feature.runtime.logic.AllocatingParticleField
import com.danyastry.performance.feature.runtime.logic.PackedParticleField
import com.danyastry.performance.feature.runtime.model.GcCounters
import com.danyastry.performance.feature.runtime.model.GcMode
import com.danyastry.performance.feature.runtime.model.GcReport

@Composable
fun GcRoute(modifier: Modifier = Modifier) {
    val viewModel: GcViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val view = LocalView.current
    val allocating = remember { AllocatingParticleField(PARTICLES, SEED) }
    val packed = remember { PackedParticleField(PARTICLES, SEED) }
    var frame by remember { mutableLongStateOf(0L) }
    LaunchedEffect(state.running) {
        if (!state.running) return@LaunchedEffect
        val field = if (state.mode == GcMode.ALLOCATE) allocating else packed
        val frames = ArrayList<Long>(FRAMES_CAPACITY)
        val first = withFrameNanos { it }
        frames += first
        var previous = first
        while (previous - first < RUN_NANOS) {
            previous = withFrameNanos { now ->
                field.step(((now - previous) / NANOS_PER_SECOND).toFloat())
                frame = now
                now
            }
            frames += previous
        }
        viewModel.onFinished(PARTICLES, frames, FrameGapStats.intervalNanos(view.display?.refreshRate ?: 0f))
    }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnvironmentBanner()
        Text(stringResource(R.string.runtime_gc_intro, PARTICLES), style = MaterialTheme.typography.bodyMedium)
        SegmentedSelector(
            options = GcMode.entries,
            selected = state.mode,
            label = { stringResource(if (it == GcMode.ALLOCATE) R.string.runtime_gc_mode_allocate else R.string.runtime_gc_mode_reuse) },
            onSelect = viewModel::onModeChange,
        )
        Text(
            stringResource(if (state.mode == GcMode.ALLOCATE) R.string.runtime_gc_allocate_explanation else R.string.runtime_gc_reuse_explanation),
            style = MaterialTheme.typography.bodySmall,
        )
        ParticleCanvas(state.mode, allocating, packed, frame)
        Button(onClick = viewModel::onStart, enabled = !state.running) {
            Text(stringResource(R.string.runtime_gc_run, RUN_SECONDS))
        }
        if (state.running) CountersCard(stringResource(R.string.runtime_gc_live), state.live, null)
        state.report?.let { CountersCard(reportTitle(it), it.counters, it) }
        Text(stringResource(R.string.runtime_gc_how_to_read), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ParticleCanvas(mode: GcMode, allocating: AllocatingParticleField, packed: PackedParticleField, frame: Long) {
    val color = MaterialTheme.colorScheme.primary
    val paint = remember(color) {
        Paint().apply {
            this.color = color.toArgb()
            strokeWidth = POINT_SIZE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
    }
    val scaled = remember { FloatArray(PARTICLES * 2) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().aspectRatio(CANVAS_RATIO).padding(8.dp)) {
            frame
            when (mode) {
                GcMode.ALLOCATE -> drawPoints(
                    points = allocating.particles.map { Offset(it.x * size.width, it.y * size.height) },
                    pointMode = PointMode.Points,
                    color = color,
                    strokeWidth = POINT_SIZE,
                    cap = StrokeCap.Round,
                )
                GcMode.REUSE -> {
                    val points = packed.points
                    for (index in 0 until PARTICLES) {
                        scaled[index * 2] = points[index * 2] * size.width
                        scaled[index * 2 + 1] = points[index * 2 + 1] * size.height
                    }
                    drawIntoCanvas { it.nativeCanvas.drawPoints(scaled, paint) }
                }
            }
        }
    }
}

@Composable
private fun reportTitle(report: GcReport): String = stringResource(
    R.string.runtime_gc_report_title,
    stringResource(if (report.mode == GcMode.ALLOCATE) R.string.runtime_gc_mode_allocate else R.string.runtime_gc_mode_reuse),
    report.seconds,
)

@Composable
private fun CountersCard(title: String, counters: GcCounters, report: GcReport?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Stat(stringResource(R.string.runtime_gc_count), counters.gcCount.toString())
                Stat(stringResource(R.string.runtime_gc_time), stringResource(R.string.runtime_ms, counters.gcTimeMillis))
                Stat(
                    stringResource(R.string.runtime_gc_blocking),
                    stringResource(R.string.runtime_gc_blocking_value, counters.blockingGcCount, counters.blockingGcTimeMillis),
                )
            }
            Stat(stringResource(R.string.runtime_gc_allocated), stringResource(R.string.runtime_mb, counters.bytesAllocated / BYTES_PER_MB))
            if (report != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Stat(stringResource(R.string.runtime_gc_rate), stringResource(R.string.runtime_mb_per_second, report.megabytesPerSecond))
                    Stat(stringResource(R.string.runtime_gc_frames), report.frames.frames.toString())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Stat(stringResource(R.string.runtime_gc_longest_gap), stringResource(R.string.runtime_ms, report.frames.longestGapMillis))
                    Stat(stringResource(R.string.runtime_gc_missed), report.frames.missedFrames.toString())
                }
            }
        }
    }
}

private const val PARTICLES = 5_000
private const val SEED = 42
private const val RUN_SECONDS = 10
private const val RUN_NANOS = RUN_SECONDS * 1_000_000_000L
private const val NANOS_PER_SECOND = 1_000_000_000.0
private const val FRAMES_CAPACITY = 1_300
private const val POINT_SIZE = 6f
private const val CANVAS_RATIO = 1.5f
private const val BYTES_PER_MB = 1024.0 * 1024.0
