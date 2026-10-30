package com.danyastry.performance.feature.startup.tracking

interface StartupClocks {
    fun processStartUptimeMillis(): Long

    fun uptimeMillis(): Long

    fun wallClockMillis(): Long
}
