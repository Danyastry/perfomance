package com.danyastry.performance.feature.mainthread.model

data class FrameGaps(
    val frames: Int,
    val longestGapMillis: Long,
    val missedFrames: Int,
)
