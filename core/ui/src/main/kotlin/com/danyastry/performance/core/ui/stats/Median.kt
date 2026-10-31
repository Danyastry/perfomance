package com.danyastry.performance.core.ui.stats

object Median {

    fun of(values: List<Long>): Long {
        require(values.isNotEmpty()) { "median of nothing" }
        val sorted = values.sorted()
        val middle = sorted.size / 2
        return if (sorted.size % 2 == 1) sorted[middle] else (sorted[middle - 1] + sorted[middle]) / 2
    }
}
