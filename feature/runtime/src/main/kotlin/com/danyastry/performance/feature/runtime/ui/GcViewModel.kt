package com.danyastry.performance.feature.runtime.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danyastry.performance.core.ui.frames.FrameGapStats
import com.danyastry.performance.feature.runtime.data.RuntimeStats
import com.danyastry.performance.feature.runtime.model.GcCounters
import com.danyastry.performance.feature.runtime.model.GcMode
import com.danyastry.performance.feature.runtime.model.GcReport
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GcViewModel(
    private val readCounters: () -> GcCounters = { RuntimeStats.read() },
) : ViewModel() {

    private val _state = MutableStateFlow(GcUiState())
    val state: StateFlow<GcUiState> = _state.asStateFlow()

    private var start = GcCounters.ZERO
    private var polling: Job? = null

    fun onModeChange(mode: GcMode) {
        if (!_state.value.running) _state.update { it.copy(mode = mode) }
    }

    fun onStart() {
        if (_state.value.running) return
        start = readCounters()
        _state.update { it.copy(running = true, live = GcCounters.ZERO) }
        polling = viewModelScope.launch {
            while (isActive) {
                delay(POLL_MILLIS)
                _state.update { it.copy(live = readCounters() - start) }
            }
        }
    }

    fun onFinished(particles: Int, frameTimesNanos: List<Long>, frameIntervalNanos: Long) {
        if (!_state.value.running) return
        polling?.cancel()
        val counters = readCounters() - start
        val seconds = if (frameTimesNanos.size < 2) 0.0 else (frameTimesNanos.last() - frameTimesNanos.first()) / NANOS_PER_SECOND
        val report = GcReport(
            mode = _state.value.mode,
            particles = particles,
            seconds = seconds,
            counters = counters,
            frames = FrameGapStats.of(frameTimesNanos, frameIntervalNanos),
        )
        _state.update { it.copy(running = false, live = counters, report = report) }
    }

    private companion object {
        const val POLL_MILLIS = 500L
        const val NANOS_PER_SECOND = 1_000_000_000.0
    }
}
