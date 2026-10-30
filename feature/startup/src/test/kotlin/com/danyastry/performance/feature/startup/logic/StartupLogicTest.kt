package com.danyastry.performance.feature.startup.logic

import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.ModeStats
import com.danyastry.performance.feature.startup.model.StartupSample
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StartupLogicTest {

    @Test
    fun `summarises launches per mode with the median`() {
        val samples = listOf(
            sample(InitMode.EAGER, 900), sample(InitMode.EAGER, 700), sample(InitMode.EAGER, 800),
            sample(InitMode.LAZY, 400), sample(InitMode.LAZY, 600),
        )

        assertThat(StartupStats.byMode(samples)).containsExactly(
            ModeStats(InitMode.EAGER, launches = 3, medianFirstFrameMillis = 800, bestFirstFrameMillis = 700),
            ModeStats(InitMode.LAZY, launches = 2, medianFirstFrameMillis = 500, bestFirstFrameMillis = 400),
        ).inOrder()
    }

    private fun sample(mode: InitMode, firstFrame: Long) = StartupSample(mode, firstFrame / 2, firstFrame, 0)
}
