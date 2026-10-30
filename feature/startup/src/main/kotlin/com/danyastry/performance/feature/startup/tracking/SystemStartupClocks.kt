package com.danyastry.performance.feature.startup.tracking

import android.os.Process
import android.os.SystemClock

object SystemStartupClocks : StartupClocks {
    override fun processStartUptimeMillis() = Process.getStartUptimeMillis()

    override fun uptimeMillis() = SystemClock.uptimeMillis()

    override fun wallClockMillis() = System.currentTimeMillis()
}
