package com.danyastry.performance.feature.startup.tracking

import android.app.Activity
import android.app.Application
import android.os.Process
import android.os.SystemClock
import android.view.ViewTreeObserver
import com.danyastry.performance.feature.startup.data.HeavyLibrary
import com.danyastry.performance.feature.startup.data.PreferencesStartupStore
import com.danyastry.performance.feature.startup.logic.StartupClock
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object StartupTracker {

    private var mode = InitMode.EAGER
    private var applicationCreated = 0L
    private var firstActivitySeen = false
    private lateinit var store: PreferencesStartupStore

    private val _lastLaunch = MutableStateFlow<StartupSample?>(null)
    val lastLaunch: StateFlow<StartupSample?> = _lastLaunch.asStateFlow()

    fun onApplicationCreate(application: Application) {
        store = PreferencesStartupStore(application)
        mode = store.mode
        when (mode) {
            InitMode.EAGER -> HeavyLibrary.initialize()
            InitMode.BACKGROUND -> Thread({ HeavyLibrary.initialize() }, "heavy-library-init").start()
            InitMode.LAZY -> Unit
        }
        applicationCreated = SystemClock.uptimeMillis()
    }

    fun onActivityCreate(activity: Activity) {
        if (firstActivitySeen || applicationCreated == 0L) return
        firstActivitySeen = true
        val decor = activity.window.decorView
        decor.viewTreeObserver.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
            private var reported = false

            override fun onDraw() {
                if (reported) return
                reported = true
                val drawn = SystemClock.uptimeMillis()
                decor.post {
                    decor.viewTreeObserver.removeOnDrawListener(this)
                    record(drawn)
                }
            }
        })
    }

    private fun record(firstFrame: Long) {
        val sample = StartupClock.sample(mode, Process.getStartUptimeMillis(), applicationCreated, firstFrame, System.currentTimeMillis())
            ?: return
        store.add(sample)
        _lastLaunch.value = sample
    }
}
