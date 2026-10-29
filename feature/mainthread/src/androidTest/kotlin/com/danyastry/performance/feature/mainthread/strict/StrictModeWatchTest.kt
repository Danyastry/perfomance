package com.danyastry.performance.feature.mainthread.strict

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.danyastry.performance.feature.mainthread.data.ReceiptFile
import com.danyastry.performance.feature.mainthread.logic.ReceiptLoader
import com.danyastry.performance.feature.mainthread.model.ViolationEntry
import com.danyastry.performance.feature.mainthread.model.ViolationKind
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 28)
class StrictModeWatchTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val violations = CopyOnWriteArrayList<ViolationEntry>()
    private val watch = StrictModeWatch { violations += it }
    private val loader = ReceiptLoader(checksumRounds = 1)
    private lateinit var directory: File

    @Before
    fun setUp() {
        directory = File(instrumentation.targetContext.cacheDir, "strict-mode-test").apply {
            deleteRecursively()
            mkdirs()
        }
        instrumentation.runOnMainSync { watch.start() }
    }

    @After
    fun tearDown() {
        instrumentation.runOnMainSync { watch.stop() }
        directory.deleteRecursively()
    }

    @Test
    fun writingTheReceiptOnTheMainThreadReportsDiskWrites() {
        instrumentation.runOnMainSync { ReceiptFile(directory, lines = 1_000).ensureWritten() }

        awaitViolations(ViolationKind.DISK_WRITE)

        assertThat(violations.map { it.thread }.toSet()).containsExactly("main")
        assertThat(violations.size).isAtMost(MAX_VIOLATIONS_PER_MESSAGE)
    }

    @Test
    fun loadingOnTheMainThreadReportsTheDiskReadAndTheSlowCall() {
        val receipt = ReceiptFile(directory, lines = 1_000).apply { ensureWritten() }

        instrumentation.runOnMainSync { loader.load(receipt) }

        awaitViolations(ViolationKind.DISK_READ, ViolationKind.SLOW_CALL)
        assertThat(violations.map { it.kind }).doesNotContain(ViolationKind.DISK_WRITE)
        assertThat(violations.first { it.kind == ViolationKind.DISK_READ }.origin).startsWith("ReceiptFile.")
        assertThat(violations.first { it.kind == ViolationKind.SLOW_CALL }.origin).isEqualTo("ReceiptLoader.load")
    }

    @Test
    fun loadingOnAWorkerThreadReportsNothing() {
        thread(name = "receipt-worker") { loader.load(ReceiptFile(directory, lines = 1_000)) }.join()
        instrumentation.waitForIdleSync()
        SystemClock.sleep(QUIET_MILLIS)

        assertThat(violations).isEmpty()
    }

    @Test
    fun stoppingRestoresTheOldPolicy() {
        instrumentation.runOnMainSync {
            watch.stop()
            loader.load(ReceiptFile(directory, lines = 10))
        }
        instrumentation.waitForIdleSync()
        SystemClock.sleep(QUIET_MILLIS)

        assertThat(violations).isEmpty()
    }

    private fun awaitViolations(vararg kinds: ViolationKind) {
        val deadline = SystemClock.uptimeMillis() + TIMEOUT_MILLIS
        while (SystemClock.uptimeMillis() < deadline && !violations.map { it.kind }.containsAll(kinds.toList())) {
            instrumentation.waitForIdleSync()
            SystemClock.sleep(POLL_MILLIS)
        }
        assertThat(violations.map { it.kind }).containsAtLeastElementsIn(kinds.toList())
    }

    private companion object {
        const val TIMEOUT_MILLIS = 5_000L
        const val POLL_MILLIS = 50L
        const val QUIET_MILLIS = 500L
        const val MAX_VIOLATIONS_PER_MESSAGE = 10
    }
}
