package com.danyastry.performance.feature.rendering.model

enum class FramePhase(val thread: PhaseThread, val stacked: Boolean) {
    UNKNOWN_DELAY(PhaseThread.UI, stacked = true),
    INPUT(PhaseThread.UI, stacked = true),
    ANIMATION(PhaseThread.UI, stacked = true),
    LAYOUT(PhaseThread.UI, stacked = true),
    DRAW(PhaseThread.UI, stacked = true),
    SYNC(PhaseThread.RENDER, stacked = true),
    COMMAND_ISSUE(PhaseThread.RENDER, stacked = true),
    SWAP_BUFFERS(PhaseThread.RENDER, stacked = true),
    GPU(PhaseThread.GPU, stacked = false),
    TOTAL(PhaseThread.WHOLE_FRAME, stacked = false),
}
