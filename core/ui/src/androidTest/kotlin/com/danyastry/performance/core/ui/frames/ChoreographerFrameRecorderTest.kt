package com.danyastry.performance.core.ui.frames

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChoreographerFrameRecorderTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val recorder = ChoreographerFrameRecorder(INTERVAL)

    @Test
    fun anIdleMainThreadDrawsEveryFrame() {
        instrumentation.runOnMainSync { recorder.start() }
        SystemClock.sleep(500)
        lateinit var gaps: FrameGaps
        instrumentation.runOnMainSync { gaps = recorder.stop() }

        assertThat(gaps.frames).isAtLeast(20)
        assertThat(gaps.longestGapMillis).isLessThan(BLOCK_MILLIS)
    }

    @Test
    fun aBlockedMainThreadShowsUpAsOneLongGap() {
        instrumentation.runOnMainSync { recorder.start() }
        SystemClock.sleep(150)
        instrumentation.runOnMainSync { SystemClock.sleep(BLOCK_MILLIS) }
        SystemClock.sleep(150)
        lateinit var gaps: FrameGaps
        instrumentation.runOnMainSync { gaps = recorder.stop() }

        assertThat(gaps.longestGapMillis).isAtLeast(BLOCK_MILLIS - 20)
        assertThat(gaps.missedFrames).isAtLeast((BLOCK_MILLIS / 17 - 2).toInt())
    }

    private companion object {
        const val INTERVAL = 16_666_667L
        const val BLOCK_MILLIS = 300L
    }
}
