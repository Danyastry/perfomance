package com.danyastry.performance.feature.snapshots.logic

import androidx.compose.runtime.SnapshotMutationPolicy

object StockMergePolicy : SnapshotMutationPolicy<Int> {

    override fun equivalent(a: Int, b: Int): Boolean = a == b

    override fun merge(previous: Int, current: Int, applied: Int): Int = current + (applied - previous)
}
