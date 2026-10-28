package com.danyastry.performance.feature.startup.model

data class StartupSample(
    val mode: InitMode,
    val applicationMillis: Long,
    val firstFrameMillis: Long,
    val timestampMillis: Long,
)
