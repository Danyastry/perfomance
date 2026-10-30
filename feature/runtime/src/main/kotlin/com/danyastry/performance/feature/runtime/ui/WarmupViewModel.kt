package com.danyastry.performance.feature.runtime.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danyastry.performance.feature.runtime.logic.WarmupRunner
import com.danyastry.performance.feature.runtime.logic.WarmupStats
import com.danyastry.performance.feature.runtime.model.WarmupRun
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WarmupViewModel(
    private val worker: CoroutineDispatcher = Dispatchers.Default,
    private val runWorkload: () -> WarmupRun = { WarmupRunner().run() },
) : ViewModel() {

    private val _state = MutableStateFlow(WarmupUiState())
    val state: StateFlow<WarmupUiState> = _state.asStateFlow()

    fun onRun() {
        if (_state.value.running) return
        _state.update { it.copy(running = true) }
        viewModelScope.launch {
            try {
                val run = withContext(worker) { runWorkload() }
                _state.update { it.copy(run = run, summary = WarmupStats.summarize(run.batchMicros)) }
            } finally {
                _state.update { it.copy(running = false) }
            }
        }
    }
}
