package com.danyastry.performance.feature.snapshots.logic

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import com.danyastry.performance.feature.snapshots.model.DemoStep

object ObservationDemo {

    fun run(): List<DemoStep> {
        val log = StepLog()
        val price = mutableIntStateOf(100)
        val name = mutableStateOf("Coffee")
        val stock = mutableIntStateOf(10)
        val labels = mapOf<Any, String>(price to "price", name to "name", stock to "stock")
        log.step("val price, name, stock = three snapshot states")

        val reads = linkedSetOf<String>()
        val label = Snapshot.observe(readObserver = { state -> labels[state]?.let(reads::add) }) {
            "${name.value}: ${price.intValue}"
        }
        log.step("Snapshot.observe(readObserver) { \"\${name.value}: \${price.value}\" }", "block returned: $label")
        log.step("reads seen by the observer", "read: ${reads.joinToString()}")

        val changed = mutableListOf<String>()
        val handle = Snapshot.registerApplyObserver { objects, _ -> objects.mapNotNullTo(changed) { labels[it] } }
        try {
            Snapshot.withMutableSnapshot { stock.intValue = 9 }
            log.step("Snapshot.withMutableSnapshot { stock.value = 9 }", "applied: ${changed.joinToString()}; affects the block: ${affects(changed, reads)}")
            changed.clear()
            Snapshot.withMutableSnapshot { price.intValue = 110 }
            log.step("Snapshot.withMutableSnapshot { price.value = 110 }", "applied: ${changed.joinToString()}; affects the block: ${affects(changed, reads)}")
        } finally {
            handle.dispose()
        }
        return log.steps()
    }

    private fun affects(changed: List<String>, reads: Set<String>): String = if (changed.any(reads::contains)) "yes" else "no"
}
