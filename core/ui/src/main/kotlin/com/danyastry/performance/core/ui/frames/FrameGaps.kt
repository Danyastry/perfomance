package com.danyastry.performance.core.ui.frames

data class FrameGaps(
    val frames: Int,
    val longestGapMillis: Long,
    val missedFrames: Int,
)
