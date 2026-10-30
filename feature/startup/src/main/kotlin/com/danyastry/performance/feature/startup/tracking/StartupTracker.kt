package com.danyastry.performance.feature.startup.tracking

import android.app.Activity
import android.app.Application
import android.view.ViewTreeObserver
import com.danyastry.performance.feature.startup.data.PreferencesStartupStore
import com.danyastry.performance.feature.startup.data.StartupLibrary
import com.danyastry.performance.feature.startup.model.StartupSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object StartupTracker {

    private var tracking: StartupTracking? = null

    private val _lastLaunch = MutableStateFlow<StartupSample?>(null)
    val lastLaunch: StateFlow<StartupSample?> = _lastLaunch.asStateFlow()

    fun onApplicationCreate(application: Application) {
        tracking = StartupTracking(
            store = PreferencesStartupStore(application),
            library = StartupLibrary.heavy,
            clocks = SystemStartupClocks,
            runInBackground = { work -> Thread(work, BACKGROUND_THREAD).start() },
            onSample = { _lastLaunch.value = it },
        ).also { it.onApplicationCreate() }
    }

    fun onActivityCreate(activity: Activity) {
        val tracking = tracking ?: return
        if (!tracking.claimFirstActivity()) return
        val decor = activity.window.decorView
        decor.viewTreeObserver.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
            private var reported = false

            override fun onDraw() {
                if (reported) return
                reported = true
                val drawn = SystemStartupClocks.uptimeMillis()
                decor.post {
                    decor.viewTreeObserver.removeOnDrawListener(this)
                    tracking.onFirstDraw(drawn)
                }
            }
        })
    }

    private const val BACKGROUND_THREAD = "heavy-library-init"
}
