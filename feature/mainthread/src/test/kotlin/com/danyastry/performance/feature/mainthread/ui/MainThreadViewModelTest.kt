package com.danyastry.performance.feature.mainthread.ui

import com.danyastry.performance.feature.mainthread.frames.FrameRecorder
import com.danyastry.performance.feature.mainthread.model.LoadMode
import com.danyastry.performance.feature.mainthread.model.ReceiptSummary
import com.danyastry.performance.feature.mainthread.model.ViolationEntry
import com.danyastry.performance.feature.mainthread.model.ViolationKind
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
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
class MainThreadViewModelTest {

    private val main = StandardTestDispatcher()
    private val summary = ReceiptSummary(lines = 10, totalCents = 1_000, distinctSkus = 5, checksum = "ab")
    private val loadThreads = mutableListOf<String>()
    private val directoryThreads = mutableListOf<String>()
    private val recorder = FakeRecorder()

    @Before
    fun setUp() {
        Dispatchers.setMain(main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `main thread mode runs the work on the main dispatcher`() = runTest(main) {
        val viewModel = viewModel(worker = WorkerDispatcher)

        viewModel.onLoad(directory, INTERVAL)
        advanceUntilIdle()

        assertThat(loadThreads).containsExactly("main")
        assertThat(directoryThreads).containsExactly("main")
        val report = viewModel.state.value.lastReport!!
        assertThat(report.mode).isEqualTo(LoadMode.MAIN_THREAD)
        assertThat(report.summary).isEqualTo(summary)
        assertThat(report.frames.missedFrames).isEqualTo(29)
        assertThat(viewModel.state.value.running).isFalse()
        assertThat(viewModel.state.value.runs).isEqualTo(1)
    }

    @Test
    fun `background mode moves the work to the worker`() = runTest(main) {
        val viewModel = viewModel(worker = WorkerDispatcher)
        viewModel.onModeChange(LoadMode.BACKGROUND)

        viewModel.onLoad(directory, INTERVAL)
        advanceUntilIdle()

        assertThat(loadThreads).containsExactly("worker")
        assertThat(directoryThreads).containsExactly("worker")
        assertThat(viewModel.state.value.lastReport!!.mode).isEqualTo(LoadMode.BACKGROUND)
    }

    @Test
    fun `a second tap while loading is ignored`() = runTest(main) {
        val viewModel = viewModel(worker = WorkerDispatcher)

        viewModel.onLoad(directory, INTERVAL)
        viewModel.onLoad(directory, INTERVAL)
        advanceUntilIdle()

        assertThat(loadThreads).hasSize(1)
    }

    @Test
    fun `a failed load shows the error and stops recording`() = runTest(main) {
        val viewModel = MainThreadViewModel(
            worker = WorkerDispatcher,
            loader = { error("disk is full") },
            recorderFactory = { recorder },
        )

        viewModel.onLoad(directory, INTERVAL)
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isEqualTo("disk is full")
        assertThat(viewModel.state.value.running).isFalse()
        assertThat(recorder.stopped).isTrue()
    }

    @Test
    fun `violations are listed newest first and can be cleared`() {
        val viewModel = viewModel(worker = WorkerDispatcher)
        val read = ViolationEntry(ViolationKind.DISK_READ, "main", "ReceiptFile.readBytes")
        val slow = ViolationEntry(ViolationKind.SLOW_CALL, "main", "ReceiptLoader.load")

        viewModel.onViolation(read)
        viewModel.onViolation(slow)

        assertThat(viewModel.state.value.violations).containsExactly(slow, read).inOrder()
        viewModel.onClearViolations()
        assertThat(viewModel.state.value.violations).isEmpty()
    }

    private val directory = {
        directoryThreads += if (WorkerDispatcher.active.get()) "worker" else "main"
        File("unused")
    }

    private fun viewModel(worker: CoroutineDispatcher) = MainThreadViewModel(
        worker = worker,
        loader = {
            loadThreads += if (WorkerDispatcher.active.get()) "worker" else "main"
            summary
        },
        recorderFactory = { recorder },
    )

    private class FakeRecorder : FrameRecorder {
        var stopped = false

        override fun start() = Unit

        override fun stop(): List<Long> {
            stopped = true
            return listOf(0L, INTERVAL, INTERVAL + 500_000_000L)
        }
    }

    private object WorkerDispatcher : CoroutineDispatcher() {
        val active = ThreadLocal.withInitial { false }

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            active.set(true)
            try {
                block.run()
            } finally {
                active.set(false)
            }
        }
    }

    private companion object {
        const val INTERVAL = 16_666_667L
    }
}
