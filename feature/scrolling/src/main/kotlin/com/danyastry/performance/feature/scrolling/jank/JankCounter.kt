package com.danyastry.performance.feature.scrolling.jank

import com.danyastry.performance.feature.scrolling.model.JankSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JankCounter {

    private val durations = ArrayDeque<Long>()
    private var frames = 0
    private var janky = 0
    private var slowest = 0L

    private val _summary = MutableStateFlow(JankSummary())
    val summary: StateFlow<JankSummary> = _summary.asStateFlow()

    @Synchronized
    fun record(durationNanos: Long, isJank: Boolean) {
        frames++
        if (isJank) janky++
        slowest = maxOf(slowest, durationNanos / NANOS_PER_MILLI)
        durations.addLast(durationNanos / NANOS_PER_MICRO)
        if (durations.size > WINDOW) durations.removeFirst()
        val sorted = durations.sorted()
        _summary.value = JankSummary(
            frames = frames,
            jankyFrames = janky,
            slowestFrameMillis = slowest,
            medianFrameMicros = percentile(sorted, 50),
            p90FrameMicros = percentile(sorted, 90),
        )
    }

    @Synchronized
    fun reset() {
        durations.clear()
        frames = 0
        janky = 0
        slowest = 0
        _summary.value = JankSummary()
    }

    private companion object {
        const val NANOS_PER_MILLI = 1_000_000L
        const val NANOS_PER_MICRO = 1_000L
        const val WINDOW = 2_000

        fun percentile(sorted: List<Long>, percent: Int): Long =
            if (sorted.isEmpty()) 0 else sorted[((sorted.size - 1) * percent) / 100]
    }
}
