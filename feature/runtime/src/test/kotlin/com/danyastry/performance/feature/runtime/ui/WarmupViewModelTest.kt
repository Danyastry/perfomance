package com.danyastry.performance.feature.runtime.ui

import com.danyastry.performance.feature.runtime.model.WarmupRun
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WarmupViewModelTest {

    private val main = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `runs the workload and summarizes it`() = runTest(main) {
        var runs = 0
        val viewModel = WarmupViewModel(worker = main, runWorkload = {
            runs++
            WarmupRun(batchMicros = listOf(400L, 100L) + List(10) { 10L }, checksum = 1, firstInProcess = true)
        })

        viewModel.onRun()
        viewModel.onRun()
        advanceUntilIdle()

        assertThat(runs).isEqualTo(1)
        assertThat(viewModel.state.value.summary!!.firstMicros).isEqualTo(400)
        assertThat(viewModel.state.value.summary!!.warmFromBatch).isEqualTo(3)
        assertThat(viewModel.state.value.running).isFalse()
    }
}
