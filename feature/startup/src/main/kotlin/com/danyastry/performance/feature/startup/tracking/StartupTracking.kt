package com.danyastry.performance.feature.startup.tracking

import com.danyastry.performance.feature.startup.data.StartupLibrary
import com.danyastry.performance.feature.startup.data.StartupStore
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample

class StartupTracking(
    private val store: StartupStore,
    private val library: StartupLibrary,
    private val clocks: StartupClocks,
    private val runInBackground: (() -> Unit) -> Unit,
    private val onSample: (StartupSample) -> Unit,
) {

    private var mode = InitMode.EAGER
    private var applicationCreated = 0L
    private var firstActivitySeen = false

    fun onApplicationCreate() {
        mode = store.mode
        when (mode) {
            InitMode.EAGER -> library.initialize()
            InitMode.BACKGROUND -> runInBackground { library.initialize() }
            InitMode.LAZY -> Unit
        }
        applicationCreated = clocks.uptimeMillis()
    }

    fun claimFirstActivity(): Boolean {
        if (firstActivitySeen || applicationCreated == 0L) return false
        firstActivitySeen = true
        return true
    }

    fun onFirstDraw(drawnUptimeMillis: Long): StartupSample? {
        val processStart = clocks.processStartUptimeMillis()
        if (processStart <= 0 || applicationCreated < processStart || drawnUptimeMillis < applicationCreated) return null
        val sample = StartupSample(
            mode = mode,
            applicationMillis = applicationCreated - processStart,
            firstFrameMillis = drawnUptimeMillis - processStart,
            timestampMillis = clocks.wallClockMillis(),
        )
        store.add(sample)
        onSample(sample)
        return sample
    }
}
