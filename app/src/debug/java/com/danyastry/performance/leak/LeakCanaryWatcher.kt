package com.danyastry.performance.leak

import com.danyastry.performance.feature.memory.watch.LeakWatcher
import leakcanary.AppWatcher
import leakcanary.LeakCanary

object LeakCanaryWatcher : LeakWatcher {
    override val isAvailable = true

    override fun retainedObjectCount() = AppWatcher.objectWatcher.retainedObjectCount

    override fun dumpHeap() = LeakCanary.dumpHeap()
}
