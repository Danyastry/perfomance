package com.danyastry.performance.feature.snapshots.logic

import com.danyastry.performance.feature.snapshots.model.SnapshotDemo
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SnapshotDemosTest {

    @Test
    fun `a write inside a mutable snapshot is invisible outside until apply`() {
        val observed = IsolationDemo.run().map { it.observed }.filter { it.isNotEmpty() }

        assertThat(observed).containsExactly(
            "global: price = 100",
            "inside: price = 120",
            "outside: price = 100",
            "result: Success",
            "outside: price = 120",
        ).inOrder()
    }

    @Test
    fun `with the default policy the second conflicting apply fails and its sale is lost`() {
        val observed = ConflictDemo.withDefaultPolicy().map { it.observed }.filter { it.isNotEmpty() }

        assertThat(observed).containsExactly(
            "global: stock = 10",
            "first sees: stock = 9",
            "second sees: stock = 8",
            "result: Success, global: stock = 9",
            "result: Failure, global: stock = 9",
        ).inOrder()
    }

    @Test
    fun `a merge policy keeps both sales`() {
        val observed = ConflictDemo.withMergePolicy().map { it.observed }.filter { it.isNotEmpty() }

        assertThat(observed.takeLast(2)).containsExactly(
            "result: Success, global: stock = 9",
            "result: Success, global: stock = 7",
        ).inOrder()
    }

    @Test
    fun `the merge policy adds the second change on top of the first`() {
        assertThat(StockMergePolicy.merge(previous = 10, current = 9, applied = 8)).isEqualTo(7)
        assertThat(StockMergePolicy.equivalent(3, 3)).isTrue()
    }

    @Test
    fun `the read observer sees only the states the block read, and only their writes affect it`() {
        val observed = ObservationDemo.run().map { it.observed }.filter { it.isNotEmpty() }

        assertThat(observed).containsExactly(
            "block returned: Coffee: 100",
            "read: name, price",
            "applied: stock; affects the block: no",
            "applied: price; affects the block: yes",
        ).inOrder()
    }

    @Test
    fun `every demo runs again from a clean start`() {
        SnapshotDemo.entries.forEach { demo ->
            assertThat(SnapshotDemos.run(demo)).isEqualTo(SnapshotDemos.run(demo))
        }
    }
}
