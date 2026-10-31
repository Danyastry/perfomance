package com.danyastry.performance.feature.rendering.model

data class PipelineSnapshot(
    val frames: Int,
    val averageMillis: Map<FramePhase, Double>,
    val recentMillis: List<Map<FramePhase, Double>>,
    val gpuAvailable: Boolean,
    val missedByMeter: Int = 0,
) {
    companion object {
        val EMPTY = PipelineSnapshot(frames = 0, averageMillis = emptyMap(), recentMillis = emptyList(), gpuAvailable = false)
    }
}
