package com.danyastry.performance.feature.snapshots.logic

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.structuralEqualityPolicy
import com.danyastry.performance.feature.snapshots.model.DemoStep

object ConflictDemo {

    fun withDefaultPolicy(): List<DemoStep> =
        run(structuralEqualityPolicy(), "val stock = mutableStateOf(10)")

    fun withMergePolicy(): List<DemoStep> =
        run(StockMergePolicy, "val stock = mutableStateOf(10, policy = StockMergePolicy)")

    private fun run(policy: SnapshotMutationPolicy<Int>, declaration: String): List<DemoStep> {
        val log = StepLog()
        val stock: MutableState<Int> = mutableStateOf(STARTING_STOCK, policy)
        log.step(declaration, "global: stock = ${stock.value}")

        val first = Snapshot.takeMutableSnapshot()
        val second = Snapshot.takeMutableSnapshot()
        log.step("val first = Snapshot.takeMutableSnapshot()")
        log.step("val second = Snapshot.takeMutableSnapshot()")
        try {
            first.enter { stock.value -= 1 }
            log.step("first.enter { stock.value -= 1 }", "first sees: stock = ${first.enter { stock.value }}")
            second.enter { stock.value -= 2 }
            log.step("second.enter { stock.value -= 2 }", "second sees: stock = ${second.enter { stock.value }}")

            log.step("first.apply()", "result: ${SnapshotResults.name(first.apply())}, global: stock = ${stock.value}")
            log.step("second.apply()", "result: ${SnapshotResults.name(second.apply())}, global: stock = ${stock.value}")
        } finally {
            first.dispose()
            second.dispose()
        }
        return log.steps()
    }

    const val STARTING_STOCK = 10
}
