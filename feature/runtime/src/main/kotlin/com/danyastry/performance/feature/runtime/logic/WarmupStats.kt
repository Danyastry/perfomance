package com.danyastry.performance.feature.runtime.logic

import com.danyastry.performance.core.ui.stats.Median
import com.danyastry.performance.feature.runtime.model.WarmupSummary

object WarmupStats {

    fun summarize(batchMicros: List<Long>, tail: Int = TAIL): WarmupSummary {
        require(batchMicros.isNotEmpty()) { "at least one batch is needed" }
        val warm = Median.of(batchMicros.takeLast(tail.coerceAtMost(batchMicros.size)))
        val first = batchMicros.first()
        return WarmupSummary(
            firstMicros = first,
            warmMicros = warm,
            speedup = first.toDouble() / warm.coerceAtLeast(1),
            warmFromBatch = batchMicros.indexOfFirst { it <= warm * WARM_FACTOR } + 1,
        )
    }

    const val TAIL = 50
    private const val WARM_FACTOR = 2
}
