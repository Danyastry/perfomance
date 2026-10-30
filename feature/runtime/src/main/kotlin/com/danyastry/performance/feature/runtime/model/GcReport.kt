package com.danyastry.performance.feature.runtime.model

import com.danyastry.performance.core.ui.frames.FrameGaps

data class GcReport(
    val mode: GcMode,
    val particles: Int,
    val seconds: Double,
    val counters: GcCounters,
    val frames: FrameGaps,
) {
    val allocatedMegabytes: Double
        get() = counters.bytesAllocated / BYTES_PER_MB

    val megabytesPerSecond: Double
        get() = if (seconds > 0) allocatedMegabytes / seconds else 0.0

    private companion object {
        const val BYTES_PER_MB = 1024.0 * 1024.0
    }
}
