package com.danyastry.performance.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danyastry.performance.core.contract.LabTags
import com.danyastry.performance.core.contract.TraceSections
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class MainThreadBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun loadReceiptOnMainThread() = load(LabTags.MAIN_THREAD_ON_MAIN, LabTags.PARSER_SPLIT)

    @Test
    fun loadReceiptInBackground() = load(LabTags.MAIN_THREAD_IN_BACKGROUND, LabTags.PARSER_SPLIT)

    @Test
    fun loadReceiptOnMainThreadWithIndexScan() = load(LabTags.MAIN_THREAD_ON_MAIN, LabTags.PARSER_SCAN)

    private fun load(modeTag: String, parserTag: String) = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()) + TraceSections.RECEIPT.map { TraceSectionMetric(it) },
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.WARM,
        iterations = ITERATIONS,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            openMainThreadScreen(modeTag, parserTag)
        },
    ) {
        loadReceipt()
    }

    private companion object {
        const val ITERATIONS = 5
    }
}
