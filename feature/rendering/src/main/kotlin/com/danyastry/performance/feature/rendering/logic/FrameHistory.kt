package com.danyastry.performance.feature.rendering.logic

import com.danyastry.performance.feature.rendering.model.FramePhase
import com.danyastry.performance.feature.rendering.model.PipelineSnapshot

class FrameHistory(
    private val window: Int = WINDOW,
    private val recent: Int = RECENT,
    private val nanoTime: () -> Long = System::nanoTime,
) : FrameMetricsSink {

    private val allPhases = FramePhase.entries
    private val values = Array(allPhases.size) { LongArray(window) }
    private var stored = 0
    private var next = 0
    private var frames = 0
    private var gpuSeen = false
    private var missed = 0
    private var resetAt = Long.MIN_VALUE

    @Synchronized
    override fun record(phases: LongArray, intendedVsyncNanos: Long, droppedBefore: Int) {
        require(phases.size == allPhases.size) { "expected ${allPhases.size} phase values, got ${phases.size}" }
        if (intendedVsyncNanos < resetAt) return
        missed += droppedBefore
        for (index in allPhases.indices) values[index][next] = phases[index]
        if (phases[FramePhase.GPU.ordinal] >= 0) gpuSeen = true
        next = (next + 1) % window
        if (stored < window) stored++
        frames++
    }

    @Synchronized
    fun reset() {
        stored = 0
        next = 0
        frames = 0
        gpuSeen = false
        missed = 0
        resetAt = nanoTime()
    }

    @Synchronized
    fun snapshot(): PipelineSnapshot {
        if (stored == 0) return PipelineSnapshot.EMPTY
        val phaseCount = allPhases.size
        val averages = DoubleArray(phaseCount)
        for (phase in 0 until phaseCount) {
            val column = values[phase]
            var sum = 0L
            var counted = 0
            for (slot in 0 until stored) {
                val value = column[slot]
                if (value >= 0) {
                    sum += value
                    counted++
                }
            }
            averages[phase] = if (counted == 0) 0.0 else sum.toDouble() / counted / NANOS_PER_MILLI
        }
        val count = minOf(recent, stored)
        val latest = DoubleArray(count * phaseCount)
        for (offset in 0 until count) {
            val slot = (next - count + offset + window) % window
            for (phase in 0 until phaseCount) latest[offset * phaseCount + phase] = values[phase][slot].coerceAtLeast(0) / NANOS_PER_MILLI
        }
        return PipelineSnapshot(
            frames = frames,
            averages = averages,
            recent = latest,
            recentCount = count,
            gpuAvailable = gpuSeen,
            missedByMeter = missed,
        )
    }

    companion object {
        const val WINDOW = 120
        const val RECENT = 60
        private const val NANOS_PER_MILLI = 1_000_000.0
    }
}
