package com.danyastry.performance.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class MainThreadBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun loadReceiptOnMainThread() = load(background = false)

    @Test
    fun loadReceiptInBackground() = load(background = true)

    private fun load(background: Boolean) = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            FrameTimingMetric(),
            TraceSectionMetric("receipt.load"),
            TraceSectionMetric("receipt.read"),
            TraceSectionMetric("receipt.parse"),
            TraceSectionMetric("receipt.checksum"),
        ),
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.WARM,
        iterations = ITERATIONS,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            openMainThreadScreen(background)
        },
    ) {
        loadReceipt()
    }

    private companion object {
        const val ITERATIONS = 5
    }
}
