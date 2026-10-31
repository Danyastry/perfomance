package com.danyastry.performance.feature.rendering.ui

import com.danyastry.performance.feature.rendering.model.FramePhase
import com.danyastry.performance.feature.rendering.model.PhaseLoad
import com.danyastry.performance.feature.rendering.model.PipelineSnapshot
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PipelineViewModelTest {

    @Test
    fun `publishes a snapshot at once and then on a fixed interval, not per frame`() = runTest {
        val viewModel = PipelineViewModel(pollMillis = 250)
        val seen = mutableListOf<PipelineSnapshot>()
        backgroundScope.launch { viewModel.snapshots.collect { seen += it } }
        runCurrent()

        viewModel.frameSink.record(frame(drawNanos = 3_000_000), intendedVsyncNanos = Long.MAX_VALUE, droppedBefore = 0)
        viewModel.frameSink.record(frame(drawNanos = 5_000_000), intendedVsyncNanos = Long.MAX_VALUE, droppedBefore = 0)
        advanceTimeBy(200)
        assertThat(seen.map { it.frames }).containsExactly(0)

        advanceTimeBy(100)
        assertThat(seen.map { it.frames }).containsExactly(0, 2).inOrder()
        assertThat(seen.last().averageMillis[FramePhase.DRAW]).isWithin(1e-9).of(4.0)
    }

    @Test
    fun `changing the load starts a fresh measurement`() = runTest {
        val viewModel = PipelineViewModel(pollMillis = 250)
        viewModel.frameSink.record(frame(drawNanos = 3_000_000), intendedVsyncNanos = Long.MAX_VALUE, droppedBefore = 0)

        viewModel.onLoadChange(PhaseLoad.DRAW)

        assertThat(viewModel.load.value).isEqualTo(PhaseLoad.DRAW)
        assertThat(viewModel.snapshots.first().frames).isEqualTo(0)
    }

    private fun frame(drawNanos: Long) = LongArray(FramePhase.entries.size).also { it[FramePhase.DRAW.ordinal] = drawNanos }
}
