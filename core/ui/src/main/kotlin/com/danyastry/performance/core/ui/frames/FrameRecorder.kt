package com.danyastry.performance.core.ui.frames

interface FrameRecorder {
    fun start()

    fun stop(): FrameGaps
}
