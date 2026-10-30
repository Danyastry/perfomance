package com.danyastry.performance.core.ui.frames

import android.view.Choreographer

class ChoreographerFrameRecorder(private val frameIntervalNanos: Long) : FrameRecorder, Choreographer.FrameCallback {

    private val frameTimes = ArrayList<Long>()
    private var recording = false

    override fun start() {
        frameTimes.clear()
        recording = true
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun stop(): FrameGaps {
        recording = false
        Choreographer.getInstance().removeFrameCallback(this)
        return FrameGapStats.of(frameTimes, frameIntervalNanos)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!recording) return
        frameTimes += frameTimeNanos
        Choreographer.getInstance().postFrameCallback(this)
    }
}
