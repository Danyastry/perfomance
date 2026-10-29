package com.danyastry.performance.feature.memory.watch

interface LeakWatcher {
    val isAvailable: Boolean

    fun retainedObjectCount(): Int

    fun dumpHeap()
}
