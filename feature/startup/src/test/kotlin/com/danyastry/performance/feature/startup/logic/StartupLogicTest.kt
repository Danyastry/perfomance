package com.danyastry.performance.feature.startup.logic

import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.ModeStats
import com.danyastry.performance.feature.startup.model.StartupSample
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StartupLogicTest {

    @Test
    fun `measures from process start`() {
        val sample = StartupClock.sample(InitMode.LAZY, processStartUptime = 1_000, applicationCreatedUptime = 1_120, firstFrameUptime = 1_450, now = 9)

        assertThat(sample).isEqualTo(StartupSample(InitMode.LAZY, applicationMillis = 120, firstFrameMillis = 450, timestampMillis = 9))
    }

    @Test
    fun `refuses marks that are out of order`() {
        assertThat(StartupClock.sample(InitMode.EAGER, 0, 10, 20, 0)).isNull()
        assertThat(StartupClock.sample(InitMode.EAGER, 100, 90, 120, 0)).isNull()
        assertThat(StartupClock.sample(InitMode.EAGER, 100, 120, 110, 0)).isNull()
    }

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
