package com.danyastry.performance.feature.mainthread.frames

import android.view.Choreographer

class ChoreographerFrameRecorder : FrameRecorder, Choreographer.FrameCallback {

    private val frameTimes = ArrayList<Long>()
    private var recording = false

    override fun start() {
        frameTimes.clear()
        recording = true
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun stop(): List<Long> {
        recording = false
        Choreographer.getInstance().removeFrameCallback(this)
        return frameTimes.toList()
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!recording) return
        frameTimes += frameTimeNanos
        Choreographer.getInstance().postFrameCallback(this)
    }
}
