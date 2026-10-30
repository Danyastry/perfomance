package com.danyastry.performance.core.ui.frames

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FrameGapStatsTest {

    private val interval = 16_666_667L

    @Test
    fun `steady frames miss nothing`() {
        val frames = List(10) { it * interval }

        assertThat(FrameGapStats.of(frames, interval)).isEqualTo(FrameGaps(frames = 10, longestGapMillis = 16, missedFrames = 0))
    }

    @Test
    fun `a 500 ms stall counts the frames that were never drawn`() {
        val frames = listOf(0L, interval, interval + 500_000_000L, 2 * interval + 500_000_000L)

        val gaps = FrameGapStats.of(frames, interval)

        assertThat(gaps.longestGapMillis).isEqualTo(500)
        assertThat(gaps.missedFrames).isEqualTo(29)
    }

    @Test
    fun `small jitter is not a missed frame`() {
        val frames = listOf(0L, interval + 3_000_000L, 2 * interval)

        assertThat(FrameGapStats.of(frames, interval).missedFrames).isEqualTo(0)
    }

    @Test
    fun `no frames gives zeros`() {
        assertThat(FrameGapStats.of(emptyList(), interval)).isEqualTo(FrameGaps(0, 0, 0))
    }

    @Test
    fun `interval follows the display refresh rate`() {
        assertThat(FrameGapStats.intervalNanos(60f)).isEqualTo(16_666_667L)
        assertThat(FrameGapStats.intervalNanos(120f)).isEqualTo(8_333_333L)
        assertThat(FrameGapStats.intervalNanos(0f)).isEqualTo(16_666_667L)
    }
}
