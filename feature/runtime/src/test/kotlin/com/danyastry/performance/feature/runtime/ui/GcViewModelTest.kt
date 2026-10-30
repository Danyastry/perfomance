package com.danyastry.performance.feature.runtime.ui

import androidx.compose.runtime.MonotonicFrameClock
import com.danyastry.performance.feature.runtime.model.GcCounters
import com.danyastry.performance.feature.runtime.model.GcMode
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GcViewModelTest {

    private val main = StandardTestDispatcher()
    private val clock = FakeFrameClock()
    private var reads = 0

    @Before
    fun setUp() {
        Dispatchers.setMain(main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `a run measures every frame for its whole length and reports the counter deltas`() = runTest(main) {
        val viewModel = viewModel()

        viewModel.onStart(INTERVAL)
        advanceUntilIdle()

        val report = viewModel.state.value.report!!
        assertThat(report.mode).isEqualTo(GcMode.ALLOCATE)
        assertThat(report.particles).isEqualTo(PARTICLES)
        assertThat(report.seconds).isWithin(0.02).of(1.0)
        assertThat(report.frames.frames).isEqualTo(clock.frames)
        assertThat(report.frames.missedFrames).isEqualTo(0)
        assertThat(report.counters).isEqualTo(GcCounters(gcCount = 2, gcTimeMillis = 20, blockingGcCount = 0, blockingGcTimeMillis = 0, bytesAllocated = 2 * MB))
        assertThat(report.megabytesPerSecond).isWithin(0.05).of(2.0)
        assertThat(viewModel.state.value.running).isFalse()
    }

    @Test
    fun `each frame moves the chosen field and publishes the frame time`() = runTest(main) {
        val viewModel = viewModel()
        viewModel.onModeChange(GcMode.REUSE)
        val before = viewModel.packed.x(0)
        val allocatingBefore = viewModel.allocating.x(0)

        viewModel.onStart(INTERVAL)
        advanceUntilIdle()

        assertThat(viewModel.packed.x(0)).isNotEqualTo(before)
        assertThat(viewModel.allocating.x(0)).isEqualTo(allocatingBefore)
        assertThat(viewModel.frame.value).isEqualTo(clock.time)
        assertThat(viewModel.state.value.report!!.mode).isEqualTo(GcMode.REUSE)
    }

    @Test
    fun `a second start and a mode change during a run are ignored`() = runTest(main) {
        val viewModel = viewModel()

        viewModel.onStart(INTERVAL)
        viewModel.onModeChange(GcMode.REUSE)
        viewModel.onStart(INTERVAL)
        advanceUntilIdle()

        assertThat(viewModel.state.value.mode).isEqualTo(GcMode.ALLOCATE)
        assertThat(reads).isEqualTo(2)
    }

    private fun viewModel() = GcViewModel(
        readCounters = {
            reads++
            GcCounters(gcCount = 10L + 2 * (reads - 1), gcTimeMillis = 100L + 20 * (reads - 1), blockingGcCount = 0, blockingGcTimeMillis = 0, bytesAllocated = (reads - 1) * 2 * MB)
        },
        frameContext = { main + clock },
        runNanos = SECOND,
        particles = PARTICLES,
    )

    private class FakeFrameClock : MonotonicFrameClock {
        var time = 0L
        var frames = 0

        override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
            yield()
            time += INTERVAL
            frames++
            return onFrame(time)
        }
    }

    private companion object {
        const val PARTICLES = 200
        const val MB = 1024L * 1024L
        const val INTERVAL = 16_666_667L
        const val SECOND = 1_000_000_000L
    }
}
