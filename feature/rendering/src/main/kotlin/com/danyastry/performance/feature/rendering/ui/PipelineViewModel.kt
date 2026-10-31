package com.danyastry.performance.feature.rendering.ui

import androidx.lifecycle.ViewModel
import com.danyastry.performance.feature.rendering.logic.FrameHistory
import com.danyastry.performance.feature.rendering.logic.FrameMetricsSink
import com.danyastry.performance.feature.rendering.model.PhaseLoad
import com.danyastry.performance.feature.rendering.model.PipelineSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class PipelineViewModel(
    private val history: FrameHistory = FrameHistory(),
    private val pollMillis: Long = POLL_MILLIS,
) : ViewModel() {

    val frameSink: FrameMetricsSink = history

    private val _load = MutableStateFlow(PhaseLoad.NONE)
    val load: StateFlow<PhaseLoad> = _load.asStateFlow()

    val snapshots: Flow<PipelineSnapshot> = flow {
        while (true) {
            emit(history.snapshot())
            delay(pollMillis)
        }
    }

    fun onLoadChange(load: PhaseLoad) {
        history.reset()
        _load.value = load
    }

    private companion object {
        const val POLL_MILLIS = 250L
    }
}
