package com.danyastry.performance.feature.rendering.logic

interface FrameMetricsSink {
    fun record(phases: LongArray, intendedVsyncNanos: Long, droppedBefore: Int)
}
