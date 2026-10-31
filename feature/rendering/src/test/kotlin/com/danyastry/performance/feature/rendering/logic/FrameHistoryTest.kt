package com.danyastry.performance.feature.rendering.logic

import com.danyastry.performance.feature.rendering.model.FramePhase
import com.danyastry.performance.feature.rendering.model.PipelineSnapshot
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class FrameHistoryTest {

    @Test
    fun `averages each phase over the recorded frames`() {
        val history = FrameHistory(window = 10, recent = 5)

        history.record(frame(draw = 2_000_000, total = 10_000_000), intendedVsyncNanos = 0, droppedBefore = 0)
        history.record(frame(draw = 4_000_000, total = 14_000_000), intendedVsyncNanos = 0, droppedBefore = 0)

        val snapshot = history.snapshot()
        assertThat(snapshot.frames).isEqualTo(2)
        assertThat(snapshot.average(FramePhase.DRAW)).isWithin(1e-9).of(3.0)
        assertThat(snapshot.average(FramePhase.TOTAL)).isWithin(1e-9).of(12.0)
        assertThat(snapshot.average(FramePhase.LAYOUT)).isEqualTo(0.0)
    }

    @Test
    fun `keeps only the newest frames and returns the recent ones oldest first`() {
        val history = FrameHistory(window = 3, recent = 2)

        listOf(1L, 2L, 3L, 4L, 5L).forEach { history.record(frame(draw = it * 1_000_000), intendedVsyncNanos = 0, droppedBefore = 0) }

        val snapshot = history.snapshot()
        assertThat(snapshot.frames).isEqualTo(5)
        assertThat(snapshot.average(FramePhase.DRAW)).isWithin(1e-9).of(4.0)
        assertThat((0 until snapshot.recentCount).map { snapshot.recent(it, FramePhase.DRAW) }).containsExactly(4.0, 5.0).inOrder()
    }

    @Test
    fun `GPU time is averaged only when the platform reports it`() {
        val history = FrameHistory(window = 4, recent = 4)

        history.record(frame(gpu = FrameMetricsSink.NOT_AVAILABLE), intendedVsyncNanos = 0, droppedBefore = 0)
        assertThat(history.snapshot().gpuAvailable).isFalse()
        assertThat(history.snapshot().average(FramePhase.GPU)).isEqualTo(0.0)

        history.record(frame(gpu = 6_000_000), intendedVsyncNanos = 0, droppedBefore = 0)
        assertThat(history.snapshot().gpuAvailable).isTrue()
        assertThat(history.snapshot().average(FramePhase.GPU)).isWithin(1e-9).of(6.0)
        assertThat(history.snapshot().recent(0, FramePhase.GPU)).isEqualTo(0.0)
    }

    @Test
    fun `reset forgets everything`() {
        val history = FrameHistory()
        history.record(frame(draw = 1), intendedVsyncNanos = 0, droppedBefore = 0)

        history.reset()

        assertThat(history.snapshot()).isSameInstanceAs(PipelineSnapshot.EMPTY)
    }

    @Test
    fun `frames that started before a reset do not reach the new averages`() {
        var now = 1_000L
        val history = FrameHistory(window = 10, recent = 10, nanoTime = { now })
        history.record(frame(draw = 9_000_000), intendedVsyncNanos = 500, droppedBefore = 0)

        now = 2_000
        history.reset()
        history.record(frame(draw = 8_000_000), intendedVsyncNanos = 1_900, droppedBefore = 0)
        history.record(frame(draw = 2_000_000), intendedVsyncNanos = 2_100, droppedBefore = 0)

        val snapshot = history.snapshot()
        assertThat(snapshot.frames).isEqualTo(1)
        assertThat(snapshot.average(FramePhase.DRAW)).isWithin(1e-9).of(2.0)
    }

    @Test
    fun `counts the reports FrameMetrics dropped and forgets them on reset`() {
        val history = FrameHistory()

        history.record(frame(), intendedVsyncNanos = 0, droppedBefore = 0)
        history.record(frame(), intendedVsyncNanos = 0, droppedBefore = 3)
        assertThat(history.snapshot().missedByMeter).isEqualTo(3)

        history.reset()
        history.record(frame(), intendedVsyncNanos = Long.MAX_VALUE, droppedBefore = 0)
        assertThat(history.snapshot().missedByMeter).isEqualTo(0)
    }

    @Test
    fun `rejects a frame with the wrong number of phases`() {
        assertThrows(IllegalArgumentException::class.java) { FrameHistory().record(LongArray(3), intendedVsyncNanos = 0, droppedBefore = 0) }
    }

    private fun frame(draw: Long = 0, total: Long = 0, gpu: Long = 0): LongArray =
        LongArray(FramePhase.entries.size).also {
            it[FramePhase.DRAW.ordinal] = draw
            it[FramePhase.TOTAL.ordinal] = total
            it[FramePhase.GPU.ordinal] = gpu
        }
}
