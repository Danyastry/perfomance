package com.danyastry.performance.feature.mainthread.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danyastry.performance.feature.mainthread.data.ReceiptFile
import com.danyastry.performance.feature.mainthread.frames.ChoreographerFrameRecorder
import com.danyastry.performance.feature.mainthread.frames.FrameRecorder
import com.danyastry.performance.feature.mainthread.logic.FrameGapStats
import com.danyastry.performance.feature.mainthread.logic.ReceiptLoader
import com.danyastry.performance.feature.mainthread.model.LoadMode
import com.danyastry.performance.feature.mainthread.model.LoadReport
import com.danyastry.performance.feature.mainthread.model.ReceiptSummary
import com.danyastry.performance.feature.mainthread.model.ViolationEntry
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainThreadViewModel(
    private val worker: CoroutineDispatcher = Dispatchers.Default,
    private val loader: (ReceiptFile) -> ReceiptSummary = ReceiptLoader()::load,
    private val recorderFactory: () -> FrameRecorder = ::ChoreographerFrameRecorder,
    private val nanoTime: () -> Long = System::nanoTime,
) : ViewModel() {

    private val _state = MutableStateFlow(MainThreadUiState())
    val state: StateFlow<MainThreadUiState> = _state.asStateFlow()

    fun onModeChange(mode: LoadMode) = _state.update { it.copy(mode = mode) }

    fun onLoad(directory: () -> File, frameIntervalNanos: Long) {
        if (_state.value.running) return
        val mode = _state.value.mode
        _state.update { it.copy(running = true, error = null) }
        viewModelScope.launch {
            val recorder = recorderFactory()
            recorder.start()
            try {
                delay(SETTLE_MILLIS)
                val start = nanoTime()
                val summary = when (mode) {
                    LoadMode.MAIN_THREAD -> loader(ReceiptFile(directory()))
                    LoadMode.BACKGROUND -> withContext(worker) { loader(ReceiptFile(directory())) }
                }
                val workMillis = (nanoTime() - start) / NANOS_PER_MILLI
                delay(SETTLE_MILLIS)
                val frames = FrameGapStats.of(recorder.stop(), frameIntervalNanos)
                _state.update {
                    it.copy(runs = it.runs + 1, lastReport = LoadReport(mode, summary, workMillis, frames))
                }
            } catch (error: Exception) {
                recorder.stop()
                if (error is CancellationException) throw error
                _state.update { it.copy(error = error.message ?: error.javaClass.simpleName) }
            } finally {
                _state.update { it.copy(running = false) }
            }
        }
    }

    fun onViolation(entry: ViolationEntry) =
        _state.update { it.copy(violations = (listOf(entry) + it.violations).take(MAX_VIOLATIONS)) }

    fun onClearViolations() = _state.update { it.copy(violations = emptyList()) }

    private companion object {
        const val SETTLE_MILLIS = 100L
        const val NANOS_PER_MILLI = 1_000_000L
        const val MAX_VIOLATIONS = 20
    }
}
