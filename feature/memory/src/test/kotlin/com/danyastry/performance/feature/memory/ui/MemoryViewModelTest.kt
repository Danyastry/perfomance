package com.danyastry.performance.feature.memory.ui

import com.danyastry.performance.feature.memory.model.MemorySnapshot
import com.danyastry.performance.feature.memory.watch.HeapUsage
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MemoryViewModelTest {

    private var listeners = 3
    private var collections = 0
    private var dumps = 0

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts with the current snapshot`() = runTest {
        val viewModel = viewModel()

        assertThat(viewModel.snapshot.value.listeners).isEqualTo(3)
    }

    @Test
    fun `garbage collection runs on the worker and then refreshes`() = runTest {
        val viewModel = viewModel(UnconfinedTestDispatcher(testScheduler))
        listeners = 1

        viewModel.onCollectGarbage()

        assertThat(collections).isEqualTo(1)
        assertThat(viewModel.snapshot.value.listeners).isEqualTo(1)
    }

    @Test
    fun `clearing listeners refreshes the snapshot`() = runTest {
        val viewModel = viewModel()

        viewModel.onClearListeners()

        assertThat(viewModel.snapshot.value.listeners).isEqualTo(0)
    }

    @Test
    fun `dump heap is forwarded to the watcher`() = runTest {
        viewModel().onDumpHeap()

        assertThat(dumps).isEqualTo(1)
    }

    private fun viewModel(worker: CoroutineDispatcher = UnconfinedTestDispatcher()) = MemoryViewModel(
        worker = worker,
        readSnapshot = { MemorySnapshot(listeners, HeapUsage(1, 2), leakCanaryAvailable = true, retainedObjects = 0) },
        collectGarbage = { collections++ },
        clearListeners = { listeners = 0 },
        dumpHeap = { dumps++ },
    )
}
