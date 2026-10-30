package com.danyastry.performance.feature.runtime.model

data class WarmupRun(
    val batchMicros: List<Long>,
    val checksum: Long,
    val firstInProcess: Boolean,
)
