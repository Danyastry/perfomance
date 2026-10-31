package com.danyastry.performance.feature.snapshots.ui

import com.danyastry.performance.feature.snapshots.model.DemoStep
import com.danyastry.performance.feature.snapshots.model.SnapshotDemo
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SnapshotsViewModelTest {

    @Test
    fun `running a demo keeps the results of the others`() {
        val viewModel = SnapshotsViewModel(runDemo = { listOf(DemoStep(it.name, "ran")) })

        viewModel.onRun(SnapshotDemo.ISOLATION)
        viewModel.onRun(SnapshotDemo.MERGE)

        assertThat(viewModel.results.value.keys).containsExactly(SnapshotDemo.ISOLATION, SnapshotDemo.MERGE)
        assertThat(viewModel.results.value.getValue(SnapshotDemo.MERGE).single().code).isEqualTo("MERGE")
    }

    @Test
    fun `the real demos all produce a transcript`() {
        val viewModel = SnapshotsViewModel()

        SnapshotDemo.entries.forEach(viewModel::onRun)

        assertThat(viewModel.results.value.values.map { it.size }).doesNotContain(0)
    }
}
