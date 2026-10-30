package com.danyastry.performance.feature.scrolling.jank

import com.danyastry.performance.feature.scrolling.model.JankSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JankCounter(
    private val nanoTime: () -> Long = System::nanoTime,
    private val publishIntervalNanos: Long = PUBLISH_INTERVAL_NANOS,
) {

    private val durations = LongArray(WINDOW)
    private val scratch = LongArray(WINDOW)
    private var stored = 0
    private var next = 0
    private var frames = 0
    private var janky = 0
    private var slowest = 0L
    private var lastPublish = 0L
    private var published = false

    private val _summary = MutableStateFlow(JankSummary())
    val summary: StateFlow<JankSummary> = _summary.asStateFlow()

    @Synchronized
    fun record(durationNanos: Long, overrunNanos: Long, isJank: Boolean) {
        frames++
        if (isJank(overrunNanos, isJank)) janky++
        slowest = maxOf(slowest, durationNanos / NANOS_PER_MILLI)
        durations[next] = durationNanos / NANOS_PER_MICRO
        next = (next + 1) % WINDOW
        if (stored < WINDOW) stored++
        val now = nanoTime()
        if (!published || now - lastPublish >= publishIntervalNanos) publish(now)
    }

    @Synchronized
    fun flush() {
        if (frames > 0) publish(nanoTime())
    }

    @Synchronized
    fun reset() {
        stored = 0
        next = 0
        frames = 0
        janky = 0
        slowest = 0
        published = false
        _summary.value = JankSummary()
    }

    private fun publish(now: Long) {
        lastPublish = now
        published = true
        System.arraycopy(durations, 0, scratch, 0, stored)
        scratch.sort(0, stored)
        _summary.value = JankSummary(
            frames = frames,
            jankyFrames = janky,
            slowestFrameMillis = slowest,
            medianFrameMicros = percentile(50),
            p90FrameMicros = percentile(90),
        )
    }

    private fun percentile(percent: Int): Long = if (stored == 0) 0 else scratch[((stored - 1) * percent) / 100]

    companion object {
        const val NO_OVERRUN = Long.MIN_VALUE
        private const val NANOS_PER_MILLI = 1_000_000L
        private const val NANOS_PER_MICRO = 1_000L
        private const val WINDOW = 2_000
        private const val PUBLISH_INTERVAL_NANOS = 250_000_000L

        fun isJank(overrunNanos: Long, fallback: Boolean): Boolean =
            if (overrunNanos == NO_OVERRUN) fallback else overrunNanos > 0
    }
}
