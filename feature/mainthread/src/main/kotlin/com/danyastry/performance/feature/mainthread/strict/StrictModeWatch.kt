package com.danyastry.performance.feature.mainthread.strict

import android.os.Build
import android.os.StrictMode
import androidx.annotation.RequiresApi
import com.danyastry.performance.feature.mainthread.logic.ViolationClassifier
import com.danyastry.performance.feature.mainthread.model.ViolationEntry
import java.util.concurrent.Executor

@RequiresApi(Build.VERSION_CODES.P)
class StrictModeWatch(private val onViolation: (ViolationEntry) -> Unit) {

    private var previous: StrictMode.ThreadPolicy? = null

    fun start() {
        if (previous != null) return
        val current = StrictMode.getThreadPolicy()
        val thread = Thread.currentThread().name
        previous = current
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder(current)
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectCustomSlowCalls()
                .penaltyLog()
                .penaltyListener(DIRECT) { violation ->
                    onViolation(ViolationClassifier.entry(violation.javaClass.simpleName, thread, violation.stackTrace))
                }
                .build(),
        )
    }

    fun stop() {
        previous?.let(StrictMode::setThreadPolicy)
        previous = null
    }

    private companion object {
        val DIRECT = Executor { it.run() }
    }
}
