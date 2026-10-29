package com.danyastry.performance.feature.memory.logic

import com.danyastry.performance.feature.memory.leak.PriceUpdates
import com.danyastry.performance.feature.memory.model.MemorySnapshot
import com.danyastry.performance.feature.memory.watch.HeapUsage
import com.danyastry.performance.feature.memory.watch.LeakWatchers

object MemoryProbe {

    fun snapshot(): MemorySnapshot {
        val watcher = LeakWatchers.current
        return MemorySnapshot(
            listeners = PriceUpdates.listenerCount,
            heap = HeapUsage.current(),
            leakCanaryAvailable = watcher.isAvailable,
            retainedObjects = watcher.retainedObjectCount(),
        )
    }

    fun collectGarbage() {
        repeat(GC_ROUNDS) {
            Runtime.getRuntime().gc()
            System.runFinalization()
        }
    }

    private const val GC_ROUNDS = 2
}
