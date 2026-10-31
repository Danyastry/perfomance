package com.danyastry.performance.feature.snapshots.logic

import com.danyastry.performance.feature.snapshots.model.DemoStep

class StepLog {

    private val steps = mutableListOf<DemoStep>()

    fun step(code: String, observed: String = "") {
        steps += DemoStep(code, observed)
    }

    fun steps(): List<DemoStep> = steps.toList()
}
