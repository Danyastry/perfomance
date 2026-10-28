package com.danyastry.performance.feature.startup.model

data class ModeStats(
    val mode: InitMode,
    val launches: Int,
    val medianFirstFrameMillis: Long,
    val bestFirstFrameMillis: Long,
)
