package com.danyastry.performance.feature.rendering.data

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.view.FrameMetrics
import android.view.Window
import com.danyastry.performance.feature.rendering.logic.FrameHistory
import com.danyastry.performance.feature.rendering.model.FramePhase

class FrameMetricsRecorder(private val history: FrameHistory) : Window.OnFrameMetricsAvailableListener {

    private val scratch = LongArray(FramePhase.entries.size)
    private var thread: HandlerThread? = null
    private var window: Window? = null

    fun attach(target: Window) {
        detach()
        val worker = HandlerThread(THREAD_NAME).apply { start() }
        thread = worker
        window = target
        target.addOnFrameMetricsAvailableListener(this, Handler(worker.looper))
    }

    fun detach() {
        window?.removeOnFrameMetricsAvailableListener(this)
        window = null
        thread?.quitSafely()
        thread = null
    }

    override fun onFrameMetricsAvailable(window: Window, frameMetrics: FrameMetrics, dropCountSinceLastInvocation: Int) {
        scratch[FramePhase.UNKNOWN_DELAY.ordinal] = frameMetrics.getMetric(FrameMetrics.UNKNOWN_DELAY_DURATION)
        scratch[FramePhase.INPUT.ordinal] = frameMetrics.getMetric(FrameMetrics.INPUT_HANDLING_DURATION)
        scratch[FramePhase.ANIMATION.ordinal] = frameMetrics.getMetric(FrameMetrics.ANIMATION_DURATION)
        scratch[FramePhase.LAYOUT.ordinal] = frameMetrics.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION)
        scratch[FramePhase.DRAW.ordinal] = frameMetrics.getMetric(FrameMetrics.DRAW_DURATION)
        scratch[FramePhase.SYNC.ordinal] = frameMetrics.getMetric(FrameMetrics.SYNC_DURATION)
        scratch[FramePhase.COMMAND_ISSUE.ordinal] = frameMetrics.getMetric(FrameMetrics.COMMAND_ISSUE_DURATION)
        scratch[FramePhase.SWAP_BUFFERS.ordinal] = frameMetrics.getMetric(FrameMetrics.SWAP_BUFFERS_DURATION)
        scratch[FramePhase.GPU.ordinal] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            frameMetrics.getMetric(FrameMetrics.GPU_DURATION)
        } else {
            FrameHistory.NOT_AVAILABLE
        }
        scratch[FramePhase.TOTAL.ordinal] = frameMetrics.getMetric(FrameMetrics.TOTAL_DURATION)
        history.record(scratch)
    }

    private companion object {
        const val THREAD_NAME = "frame-metrics"
    }
}
