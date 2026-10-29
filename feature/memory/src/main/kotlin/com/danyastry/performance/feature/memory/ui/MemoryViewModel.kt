package com.danyastry.performance.feature.memory.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danyastry.performance.feature.memory.leak.PriceUpdates
import com.danyastry.performance.feature.memory.logic.MemoryProbe
import com.danyastry.performance.feature.memory.model.MemorySnapshot
import com.danyastry.performance.feature.memory.watch.LeakWatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MemoryViewModel(
    private val worker: CoroutineDispatcher = Dispatchers.Default,
    private val readSnapshot: () -> MemorySnapshot = MemoryProbe::snapshot,
    private val collectGarbage: () -> Unit = MemoryProbe::collectGarbage,
    private val clearListeners: () -> Unit = PriceUpdates::clear,
    private val dumpHeap: () -> Unit = { LeakWatchers.current.dumpHeap() },
) : ViewModel() {

    private val _snapshot = MutableStateFlow(readSnapshot())
    val snapshot: StateFlow<MemorySnapshot> = _snapshot.asStateFlow()

    fun refresh() {
        _snapshot.value = readSnapshot()
    }

    fun onCollectGarbage() {
        viewModelScope.launch {
            withContext(worker) { collectGarbage() }
            refresh()
        }
    }

    fun onClearListeners() {
        clearListeners()
        refresh()
    }

    fun onDumpHeap() = dumpHeap()
}
