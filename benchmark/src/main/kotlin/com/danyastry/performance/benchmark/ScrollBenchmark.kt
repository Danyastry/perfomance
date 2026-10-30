package com.danyastry.performance.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danyastry.performance.core.contract.LabTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScrollBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun scrollHeavyFeed() = scroll(LabTags.SCROLLING_HEAVY)

    @Test
    fun scrollOptimizedFeed() = scroll(LabTags.SCROLLING_OPTIMIZED)

    private fun scroll(modeTag: String) = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.WARM,
        iterations = ITERATIONS,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            openScrollingScreen(modeTag)
        },
    ) {
        flingFeed()
    }

    private companion object {
        const val ITERATIONS = 5
    }
}
