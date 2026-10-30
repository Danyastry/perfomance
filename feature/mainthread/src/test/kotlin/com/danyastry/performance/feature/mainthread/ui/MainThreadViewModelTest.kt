package com.danyastry.performance.feature.mainthread.ui

import com.danyastry.performance.core.ui.frames.FrameGaps
import com.danyastry.performance.core.ui.frames.FrameRecorder
import com.danyastry.performance.feature.mainthread.model.LoadMode
import com.danyastry.performance.feature.mainthread.model.ParserKind
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
    private val parsers = mutableListOf<ParserKind>()
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

        viewModel.onLoad(directory, recorder)
        advanceUntilIdle()

        assertThat(loadThreads).containsExactly("main")
        assertThat(directoryThreads).containsExactly("main")
        val report = viewModel.state.value.lastReport!!
        assertThat(report.mode).isEqualTo(LoadMode.MAIN_THREAD)
        assertThat(report.summary).isEqualTo(summary)
        assertThat(report.frames).isEqualTo(FrameGaps(frames = 3, longestGapMillis = 500, missedFrames = 29))
        assertThat(recorder.started).isTrue()
        assertThat(viewModel.state.value.running).isFalse()
        assertThat(viewModel.state.value.runs).isEqualTo(1)
    }

    @Test
    fun `background mode moves the work to the worker`() = runTest(main) {
        val viewModel = viewModel(worker = WorkerDispatcher)
        viewModel.onModeChange(LoadMode.BACKGROUND)

        viewModel.onLoad(directory, recorder)
        advanceUntilIdle()

        assertThat(loadThreads).containsExactly("worker")
        assertThat(directoryThreads).containsExactly("worker")
        assertThat(viewModel.state.value.lastReport!!.mode).isEqualTo(LoadMode.BACKGROUND)
    }

    @Test
    fun `the chosen parser is used and reported`() = runTest(main) {
        val viewModel = viewModel(worker = WorkerDispatcher)
        viewModel.onParserChange(ParserKind.SCAN)

        viewModel.onLoad(directory, recorder)
        advanceUntilIdle()

        assertThat(parsers).containsExactly(ParserKind.SCAN)
        assertThat(viewModel.state.value.lastReport!!.parser).isEqualTo(ParserKind.SCAN)
    }

    @Test
    fun `a second tap while loading is ignored`() = runTest(main) {
        val viewModel = viewModel(worker = WorkerDispatcher)

        viewModel.onLoad(directory, recorder)
        viewModel.onLoad(directory, recorder)
        advanceUntilIdle()

        assertThat(loadThreads).hasSize(1)
    }

    @Test
    fun `a failed load shows the error and stops recording`() = runTest(main) {
        val viewModel = MainThreadViewModel(
            worker = WorkerDispatcher,
            loader = { _, _ -> error("disk is full") },
        )

        viewModel.onLoad(directory, recorder)
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
        loader = { _, kind ->
            parsers += kind
            loadThreads += if (WorkerDispatcher.active.get()) "worker" else "main"
            summary
        },
    )

    private class FakeRecorder : FrameRecorder {
        var started = false
        var stopped = false

        override fun start() {
            started = true
        }

        override fun stop(): FrameGaps {
            stopped = true
            return FrameGaps(frames = 3, longestGapMillis = 500, missedFrames = 29)
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

}
