package com.danyastry.performance.feature.snapshots.logic

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshots.Snapshot
import com.danyastry.performance.feature.snapshots.model.DemoStep

object IsolationDemo {

    fun run(): List<DemoStep> {
        val log = StepLog()
        val price = mutableIntStateOf(100)
        log.step("val price = mutableIntStateOf(100)", "global: price = ${price.intValue}")

        val snapshot = Snapshot.takeMutableSnapshot()
        log.step("val snapshot = Snapshot.takeMutableSnapshot()")
        try {
            snapshot.enter { price.intValue = 120 }
            log.step("snapshot.enter { price.value = 120 }")
            log.step("snapshot.enter { price.value }", "inside: price = ${snapshot.enter { price.intValue }}")
            log.step("price.value", "outside: price = ${price.intValue}")

            val result = snapshot.apply()
            log.step("snapshot.apply()", "result: ${SnapshotResults.name(result)}")
            log.step("price.value", "outside: price = ${price.intValue}")
        } finally {
            snapshot.dispose()
        }
        return log.steps()
    }
}
