package com.danyastry.performance.feature.scrolling.jank

import com.danyastry.performance.feature.scrolling.model.JankSummary
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class JankCounterTest {

    private var now = 0L
    private val counter = JankCounter(nanoTime = { now }, publishIntervalNanos = 250_000_000L)

    @Test
    fun `counts frames, janky frames and the slowest frame`() {
        counter.record(8_000_000, overrunNanos = -8_000_000, isJank = false)
        counter.record(42_000_000, overrunNanos = 26_000_000, isJank = false)
        counter.record(12_000_000, overrunNanos = -4_000_000, isJank = false)
        counter.record(25_000_000, overrunNanos = 9_000_000, isJank = false)
        counter.flush()

        assertThat(counter.summary.value).isEqualTo(
            JankSummary(frames = 4, jankyFrames = 2, slowestFrameMillis = 42, medianFrameMicros = 12_000, p90FrameMicros = 25_000),
        )
        assertThat(counter.summary.value.jankyPercent).isEqualTo(50)
        counter.reset()
        assertThat(counter.summary.value).isEqualTo(JankSummary())
    }

    @Test
    fun `on API 31 and later the overrun decides, before that JankStats does`() {
        assertThat(JankCounter.isJank(overrunNanos = 1, fallback = false)).isTrue()
        assertThat(JankCounter.isJank(overrunNanos = 0, fallback = true)).isFalse()
        assertThat(JankCounter.isJank(overrunNanos = -5_000_000, fallback = true)).isFalse()
        assertThat(JankCounter.isJank(overrunNanos = JankCounter.NO_OVERRUN, fallback = true)).isTrue()
        assertThat(JankCounter.isJank(overrunNanos = JankCounter.NO_OVERRUN, fallback = false)).isFalse()
    }

    @Test
    fun `publishes the first frame at once and then at most once per interval`() {
        counter.record(10_000_000, JankCounter.NO_OVERRUN, isJank = false)
        assertThat(counter.summary.value.frames).isEqualTo(1)

        now += 100_000_000
        counter.record(10_000_000, JankCounter.NO_OVERRUN, isJank = false)
        now += 100_000_000
        counter.record(10_000_000, JankCounter.NO_OVERRUN, isJank = false)
        assertThat(counter.summary.value.frames).isEqualTo(1)

        now += 60_000_000
        counter.record(10_000_000, JankCounter.NO_OVERRUN, isJank = false)
        assertThat(counter.summary.value.frames).isEqualTo(4)
    }

    @Test
    fun `flush publishes what the throttle held back`() {
        counter.record(10_000_000, JankCounter.NO_OVERRUN, isJank = false)
        counter.record(30_000_000, JankCounter.NO_OVERRUN, isJank = true)

        counter.flush()

        assertThat(counter.summary.value.frames).isEqualTo(2)
        assertThat(counter.summary.value.jankyFrames).isEqualTo(1)
    }

    @Test
    fun `percentiles cover only the most recent 2000 frames`() {
        repeat(2_000) { counter.record(50_000_000, JankCounter.NO_OVERRUN, isJank = false) }
        repeat(2_000) { counter.record(5_000_000, JankCounter.NO_OVERRUN, isJank = false) }
        counter.flush()

        assertThat(counter.summary.value.frames).isEqualTo(4_000)
        assertThat(counter.summary.value.medianFrameMicros).isEqualTo(5_000)
        assertThat(counter.summary.value.p90FrameMicros).isEqualTo(5_000)
        assertThat(counter.summary.value.slowestFrameMillis).isEqualTo(50)
    }

    @Test
    fun `flush on an empty meter keeps the empty summary`() {
        counter.flush()

        assertThat(counter.summary.value).isEqualTo(JankSummary())
    }

    @Test
    fun `an empty summary has no jank`() {
        assertThat(JankSummary().jankyPercent).isEqualTo(0)
    }
}
