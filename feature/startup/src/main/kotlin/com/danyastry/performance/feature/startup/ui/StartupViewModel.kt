package com.danyastry.performance.feature.startup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danyastry.performance.feature.startup.data.StartupStore
import com.danyastry.performance.feature.startup.logic.StartupStats
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.ProfileStatus
import com.danyastry.performance.feature.startup.model.StartupSample
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartupViewModel(
    private val store: StartupStore,
    lastLaunch: StateFlow<StartupSample?>,
    private val library: StartupLibrary,
    private val worker: CoroutineDispatcher = Dispatchers.Default,
    private val clock: () -> Long = System::nanoTime,
    private val profileStatus: suspend () -> ProfileStatus? = { null },
) : ViewModel() {

    private val _state = MutableStateFlow(
        StartupUiState(
            selectedMode = store.mode,
            launchedWith = lastLaunch.value?.mode,
            lastLaunch = lastLaunch.value,
            stats = StartupStats.byMode(store.samples()),
            libraryReady = library.isReady(),
        ),
    )
    val state: StateFlow<StartupUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val status = profileStatus()
            _state.update { it.copy(profileStatus = status) }
        }
        viewModelScope.launch {
            lastLaunch.collect { sample ->
                if (sample != null) {
                    _state.update { it.copy(lastLaunch = sample, launchedWith = sample.mode, stats = StartupStats.byMode(store.samples())) }
                }
            }
        }
    }

    fun onModeChange(mode: InitMode) {
        store.mode = mode
        _state.update { it.copy(selectedMode = mode) }
    }

    fun onUseLibrary() {
        if (_state.value.usingLibrary) return
        val wasReady = library.isReady()
        _state.update { it.copy(usingLibrary = true) }
        viewModelScope.launch {
            val started = clock()
            withContext(worker) { library.initialize() }
            val cost = if (wasReady) 0L else (clock() - started) / NANOS_PER_MILLI
            _state.update { it.copy(usingLibrary = false, libraryReady = true, firstUseMillis = cost) }
        }
    }

    fun onClearHistory() {
        store.clear()
        _state.update { it.copy(stats = emptyList()) }
    }

    private companion object {
        const val NANOS_PER_MILLI = 1_000_000L
    }
}
