package com.danyastry.performance.feature.memory.watch

object LeakWatchers {

    @Volatile
    var current: LeakWatcher = NoLeakWatcher
        private set

    fun install(watcher: LeakWatcher) {
        current = watcher
    }
}
