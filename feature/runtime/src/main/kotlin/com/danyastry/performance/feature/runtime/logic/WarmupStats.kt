package com.danyastry.performance.feature.runtime.logic

import com.danyastry.performance.feature.runtime.model.WarmupSummary

object WarmupStats {

    fun summarize(batchMicros: List<Long>, tail: Int = TAIL): WarmupSummary {
        require(batchMicros.isNotEmpty()) { "at least one batch is needed" }
        val warm = median(batchMicros.takeLast(tail.coerceAtMost(batchMicros.size)))
        val first = batchMicros.first()
        return WarmupSummary(
            firstMicros = first,
            warmMicros = warm,
            speedup = first.toDouble() / warm.coerceAtLeast(1),
            warmFromBatch = batchMicros.indexOfFirst { it <= warm * WARM_FACTOR } + 1,
        )
    }

    fun median(values: List<Long>): Long {
        require(values.isNotEmpty()) { "median of nothing" }
        val sorted = values.sorted()
        val middle = sorted.size / 2
        return if (sorted.size % 2 == 1) sorted[middle] else (sorted[middle - 1] + sorted[middle]) / 2
    }

    const val TAIL = 50
    private const val WARM_FACTOR = 2
}
