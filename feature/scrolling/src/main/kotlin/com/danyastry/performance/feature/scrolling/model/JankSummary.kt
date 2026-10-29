package com.danyastry.performance.feature.scrolling.model

data class JankSummary(
    val frames: Int = 0,
    val jankyFrames: Int = 0,
    val slowestFrameMillis: Long = 0,
    val medianFrameMicros: Long = 0,
    val p90FrameMicros: Long = 0,
) {
    val jankyPercent: Int
        get() = if (frames == 0) 0 else jankyFrames * 100 / frames
}
