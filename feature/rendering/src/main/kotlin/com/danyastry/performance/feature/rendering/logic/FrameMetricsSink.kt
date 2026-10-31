package com.danyastry.performance.feature.rendering.logic

interface FrameMetricsSink {
    fun record(phases: LongArray, intendedVsyncNanos: Long, droppedBefore: Int)

    companion object {
        const val NOT_AVAILABLE = -1L
    }
}
