package com.danyastry.performance.feature.mainthread.logic

import com.danyastry.performance.feature.mainthread.model.FrameGaps
import kotlin.math.roundToLong

object FrameGapStats {

    fun of(frameTimesNanos: List<Long>, frameIntervalNanos: Long): FrameGaps {
        require(frameIntervalNanos > 0) { "frame interval must be positive, was $frameIntervalNanos" }
        val gaps = frameTimesNanos.zipWithNext { earlier, later -> later - earlier }
        val missed = gaps.sumOf { gap -> ((gap.toDouble() / frameIntervalNanos).roundToLong() - 1).coerceAtLeast(0) }
        return FrameGaps(
            frames = frameTimesNanos.size,
            longestGapMillis = (gaps.maxOrNull() ?: 0L) / NANOS_PER_MILLI,
            missedFrames = missed.toInt(),
        )
    }

    fun intervalNanos(refreshRate: Float): Long =
        if (refreshRate > 0f) (NANOS_PER_SECOND / refreshRate).roundToLong() else DEFAULT_INTERVAL_NANOS

    private const val NANOS_PER_MILLI = 1_000_000L
    private const val NANOS_PER_SECOND = 1_000_000_000.0
    private const val DEFAULT_INTERVAL_NANOS = 16_666_667L
}
