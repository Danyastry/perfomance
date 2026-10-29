package com.danyastry.performance.feature.memory.model

import com.danyastry.performance.feature.memory.watch.HeapUsage

data class MemorySnapshot(
    val listeners: Int,
    val heap: HeapUsage,
    val leakCanaryAvailable: Boolean,
    val retainedObjects: Int,
)
