package com.danyastry.performance.feature.rendering.ui

import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.activity.compose.setContent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danyastry.performance.feature.rendering.data.FrameMetricsRecorder
import com.danyastry.performance.feature.rendering.logic.BusyWork
import com.danyastry.performance.feature.rendering.logic.FrameHistory
import com.danyastry.performance.feature.rendering.model.FramePhase
import com.danyastry.performance.feature.rendering.model.PhaseLoad
import com.danyastry.performance.feature.rendering.model.PipelineSnapshot
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhaseLoadTest {

    private val history = FrameHistory()
    private val recorder = FrameMetricsRecorder(history)
    private var load by mutableStateOf(PhaseLoad.NONE)

    private lateinit var scenario: ActivityScenario<ComponentActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(ComponentActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent { PhaseLoadArea(load) }
            recorder.attach(activity.window)
        }
    }

    @After
    fun tearDown() {
        scenario.onActivity { recorder.detach() }
        scenario.close()
    }

    @Test
    fun slowCompositionIsReportedAsAnimation() {
        val averages = measure(PhaseLoad.COMPOSITION)

        assertThat(averages.average(FramePhase.ANIMATION)).isAtLeast(LOADED)
        assertThat(averages.average(FramePhase.DRAW)).isLessThan(LOADED)
    }

    @Test
    fun slowDrawIsReportedAsDraw() {
        val averages = measure(PhaseLoad.DRAW)

        assertThat(averages.average(FramePhase.DRAW)).isAtLeast(LOADED)
        assertThat(averages.average(FramePhase.ANIMATION)).isLessThan(LOADED)
    }

    @Test
    fun aComposeRemeasureIsReportedAsDrawNotAsLayout() {
        val averages = measure(PhaseLoad.LAYOUT)

        assertThat(averages.average(FramePhase.DRAW)).isAtLeast(LOADED)
        assertThat(averages.average(FramePhase.LAYOUT)).isLessThan(QUIET)
    }

    @Test
    fun theRenderThreadLoadStaysOffTheUiThread() {
        val idle = measure(PhaseLoad.NONE)
        val loaded = measure(PhaseLoad.RENDER_THREAD)

        assertThat(loaded.average(FramePhase.DRAW)).isLessThan(QUIET)
        assertThat(loaded.average(FramePhase.ANIMATION)).isLessThan(QUIET)
        assertThat(loaded.average(FramePhase.COMMAND_ISSUE)).isAtLeast(idle.average(FramePhase.COMMAND_ISSUE) + RENDER_EXTRA)
    }

    @Test
    fun withoutLoadTheUiThreadPhasesStayQuiet() {
        val averages = measure(PhaseLoad.NONE)

        assertThat(averages.average(FramePhase.ANIMATION)).isLessThan(LOADED)
        assertThat(averages.average(FramePhase.DRAW)).isLessThan(LOADED)
    }

    private fun measure(target: PhaseLoad): PipelineSnapshot {
        scenario.onActivity { load = target }
        SystemClock.sleep(SETTLE_MILLIS)
        history.reset()
        SystemClock.sleep(MEASURE_MILLIS)
        val snapshot = history.snapshot()
        assertThat(snapshot.frames).isAtLeast(MIN_FRAMES)
        return snapshot
    }

    private companion object {
        const val SETTLE_MILLIS = 300L
        const val MEASURE_MILLIS = 1_500L
        const val MIN_FRAMES = 20
        val LOADED = BusyWork.LOAD_MILLIS * 0.85
        const val QUIET = 2.0
        const val RENDER_EXTRA = 1.0
    }
}
