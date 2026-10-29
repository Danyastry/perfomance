package com.danyastry.performance.feature.memory.watch

object NoLeakWatcher : LeakWatcher {
    override val isAvailable = false

    override fun retainedObjectCount() = 0

    override fun dumpHeap() = Unit
}
