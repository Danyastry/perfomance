package com.danyastry.performance.feature.runtime.ui

import com.danyastry.performance.feature.runtime.model.GcCounters
import com.danyastry.performance.feature.runtime.model.GcMode
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GcViewModelTest {

    private val main = StandardTestDispatcher()
    private var counters = GcCounters(gcCount = 10, gcTimeMillis = 100, blockingGcCount = 0, blockingGcTimeMillis = 0, bytesAllocated = 0)

    @Before
    fun setUp() {
        Dispatchers.setMain(main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `live counters show only what happened since the start`() = runTest(main) {
        val viewModel = GcViewModel(readCounters = { counters })
        viewModel.onStart()

        counters = counters.copy(gcCount = 13, bytesAllocated = 3 * MB)
        advanceTimeBy(600)

        assertThat(viewModel.state.value.live.gcCount).isEqualTo(3)
        assertThat(viewModel.state.value.live.bytesAllocated).isEqualTo(3 * MB)
        viewModel.onFinished(100, listOf(0L, SECOND), INTERVAL)
    }

    @Test
    fun `finishing builds a report with rates and frame gaps`() = runTest(main) {
        val viewModel = GcViewModel(readCounters = { counters })
        viewModel.onModeChange(GcMode.REUSE)
        viewModel.onStart()
        counters = counters.copy(gcCount = 11, gcTimeMillis = 130, bytesAllocated = 20 * MB)

        viewModel.onFinished(5_000, listOf(0L, INTERVAL, 2 * INTERVAL, 2 * SECOND), INTERVAL)

        val report = viewModel.state.value.report!!
        assertThat(report.mode).isEqualTo(GcMode.REUSE)
        assertThat(report.particles).isEqualTo(5_000)
        assertThat(report.counters).isEqualTo(GcCounters(1, 30, 0, 0, 20 * MB))
        assertThat(report.seconds).isWithin(1e-9).of(2.0)
        assertThat(report.megabytesPerSecond).isWithin(1e-9).of(10.0)
        assertThat(report.frames.frames).isEqualTo(4)
        assertThat(viewModel.state.value.running).isFalse()
    }

    @Test
    fun `the mode cannot change during a run`() = runTest(main) {
        val viewModel = GcViewModel(readCounters = { counters })
        viewModel.onStart()

        viewModel.onModeChange(GcMode.REUSE)

        assertThat(viewModel.state.value.mode).isEqualTo(GcMode.ALLOCATE)
        viewModel.onFinished(1, listOf(0L), INTERVAL)
    }

    private companion object {
        const val MB = 1024L * 1024L
        const val INTERVAL = 16_666_667L
        const val SECOND = 1_000_000_000L
    }
}
