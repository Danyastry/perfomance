package com.danyastry.performance

import android.app.Application
import com.danyastry.performance.feature.startup.tracking.StartupTracker

class PerformanceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        StartupTracker.onApplicationCreate(this)
    }
}
