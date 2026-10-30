package com.danyastry.performance.feature.rendering.logic

import com.danyastry.performance.feature.rendering.model.FramePhase
import com.danyastry.performance.feature.rendering.model.PipelineSnapshot

class FrameHistory(private val window: Int = WINDOW, private val recent: Int = RECENT) {

    private val phases = FramePhase.entries
    private val values = Array(phases.size) { LongArray(window) }
    private var stored = 0
    private var next = 0
    private var frames = 0
    private var gpuSeen = false

    @Synchronized
    fun record(frame: LongArray) {
        require(frame.size == phases.size) { "expected ${phases.size} phase values, got ${frame.size}" }
        for (index in phases.indices) values[index][next] = frame[index]
        if (frame[FramePhase.GPU.ordinal] >= 0) gpuSeen = true
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
    }

    @Synchronized
    fun snapshot(): PipelineSnapshot {
        if (stored == 0) return PipelineSnapshot.EMPTY
        val averages = phases.associateWith { phase ->
            val column = values[phase.ordinal]
            var sum = 0L
            var counted = 0
            for (slot in 0 until stored) {
                val value = column[slot]
                if (value >= 0) {
                    sum += value
                    counted++
                }
            }
            if (counted == 0) 0.0 else sum.toDouble() / counted / NANOS_PER_MILLI
        }
        val count = minOf(recent, stored)
        val latest = List(count) { offset ->
            val slot = (next - count + offset + window) % window
            phases.associateWith { phase -> values[phase.ordinal][slot].coerceAtLeast(0) / NANOS_PER_MILLI }
        }
        return PipelineSnapshot(frames = frames, averageMillis = averages, recentMillis = latest, gpuAvailable = gpuSeen)
    }

    companion object {
        const val WINDOW = 120
        const val RECENT = 60
        const val NOT_AVAILABLE = -1L
        private const val NANOS_PER_MILLI = 1_000_000.0
    }
}
