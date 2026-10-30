package com.danyastry.performance.feature.runtime.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danyastry.performance.core.ui.frames.FrameGapStats
import com.danyastry.performance.feature.runtime.data.RuntimeStats
import com.danyastry.performance.feature.runtime.logic.AllocatingParticleField
import com.danyastry.performance.feature.runtime.logic.PackedParticleField
import com.danyastry.performance.feature.runtime.model.GcCounters
import com.danyastry.performance.feature.runtime.model.GcMode
import com.danyastry.performance.feature.runtime.model.GcReport
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GcViewModel(
    private val readCounters: () -> GcCounters = { RuntimeStats.read() },
    private val frameContext: () -> CoroutineContext = { AndroidUiDispatcher.Main },
    private val runNanos: Long = RUN_SECONDS * NANOS_PER_SECOND,
    val particles: Int = PARTICLES,
) : ViewModel() {

    val allocating = AllocatingParticleField(particles, SEED)
    val packed = PackedParticleField(particles, SEED)

    private val _state = MutableStateFlow(GcUiState())
    val state: StateFlow<GcUiState> = _state.asStateFlow()

    private val _frame = mutableLongStateOf(0L)
    val frame: State<Long> = _frame

    fun onModeChange(mode: GcMode) {
        if (!_state.value.running) _state.update { it.copy(mode = mode) }
    }

    fun onStart(frameIntervalNanos: Long) {
        if (_state.value.running) return
        val mode = _state.value.mode
        val start = readCounters()
        _state.update { it.copy(running = true, live = GcCounters.ZERO) }
        viewModelScope.launch(frameContext()) {
            val polling = launch {
                while (isActive) {
                    delay(POLL_MILLIS)
                    _state.update { it.copy(live = readCounters() - start) }
                }
            }
            val frames = runFrames(mode)
            polling.cancel()
            val counters = readCounters() - start
            val report = GcReport(
                mode = mode,
                particles = particles,
                seconds = (frames.last() - frames.first()) / NANOS_PER_SECOND.toDouble(),
                counters = counters,
                frames = FrameGapStats.of(frames, frameIntervalNanos),
            )
            _state.update { it.copy(running = false, live = counters, report = report) }
        }
    }

    private suspend fun runFrames(mode: GcMode): List<Long> {
        val field = if (mode == GcMode.ALLOCATE) allocating else packed
        val frames = ArrayList<Long>(FRAMES_CAPACITY)
        val first = withFrameNanos { it }
        frames += first
        var previous = first
        while (previous - first < runNanos) {
            previous = withFrameNanos { now ->
                field.step(((now - previous) / NANOS_PER_SECOND.toDouble()).toFloat())
                _frame.longValue = now
                now
            }
            frames += previous
        }
        return frames
    }

    companion object {
        const val PARTICLES = 5_000
        const val RUN_SECONDS = 10L
        private const val SEED = 42
        private const val POLL_MILLIS = 500L
        private const val NANOS_PER_SECOND = 1_000_000_000L
        private const val FRAMES_CAPACITY = 1_300
    }
}
