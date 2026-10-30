package com.danyastry.performance.feature.runtime.model

data class WarmupSummary(
    val firstMicros: Long,
    val warmMicros: Long,
    val speedup: Double,
    val warmFromBatch: Int,
)
