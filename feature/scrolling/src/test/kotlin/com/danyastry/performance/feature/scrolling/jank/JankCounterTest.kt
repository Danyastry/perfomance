package com.danyastry.performance.feature.scrolling.jank

import com.danyastry.performance.feature.scrolling.model.JankSummary
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class JankCounterTest {

    @Test
    fun `counts frames, janky frames and the slowest frame`() {
        val counter = JankCounter()

        counter.record(8_000_000, isJank = false)
        counter.record(42_000_000, isJank = true)
        counter.record(12_000_000, isJank = false)
        counter.record(25_000_000, isJank = true)

        assertThat(counter.summary.value).isEqualTo(
            JankSummary(frames = 4, jankyFrames = 2, slowestFrameMillis = 42, medianFrameMicros = 12_000, p90FrameMicros = 25_000),
        )
        assertThat(counter.summary.value.jankyPercent).isEqualTo(50)
        counter.reset()
        assertThat(counter.summary.value).isEqualTo(JankSummary())
    }

    @Test
    fun `an empty summary has no jank`() {
        assertThat(JankSummary().jankyPercent).isEqualTo(0)
    }
}
