package com.danyastry.performance.feature.rendering.model

class PipelineSnapshot(
    val frames: Int,
    private val averages: DoubleArray,
    private val recent: DoubleArray,
    val recentCount: Int,
    val gpuAvailable: Boolean,
    val missedByMeter: Int,
) {
    init {
        require(averages.size == PHASES) { "expected $PHASES averages, got ${averages.size}" }
        require(recent.size == recentCount * PHASES) { "expected ${recentCount * PHASES} recent values, got ${recent.size}" }
    }

    fun average(phase: FramePhase): Double = averages[phase.ordinal]

    fun recent(frame: Int, phase: FramePhase): Double = recent[frame * PHASES + phase.ordinal]

    companion object {
        private val PHASES = FramePhase.entries.size

        val EMPTY = PipelineSnapshot(
            frames = 0,
            averages = DoubleArray(PHASES),
            recent = DoubleArray(0),
            recentCount = 0,
            gpuAvailable = false,
            missedByMeter = 0,
        )
    }
}
