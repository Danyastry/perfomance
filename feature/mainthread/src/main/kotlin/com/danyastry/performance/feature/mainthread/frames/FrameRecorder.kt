package com.danyastry.performance.feature.mainthread.frames

interface FrameRecorder {
    fun start()

    fun stop(): List<Long>
}
