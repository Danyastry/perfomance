package com.danyastry.performance.feature.recomposition.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danyastry.performance.feature.recomposition.model.ListItem
import com.danyastry.performance.feature.recomposition.tracking.RecompositionCounters
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RecompositionViewModel(private val tickMillis: Long = DEFAULT_TICK_MILLIS) : ViewModel() {

    val counters = RecompositionCounters()

    var tick by mutableIntStateOf(0)
        private set

    var running by mutableStateOf(false)
        private set

    val items = mutableStateListOf<ListItem>().apply { addAll((1..INITIAL_ITEMS).map { ListItem(it, "Item $it") }) }

    val level: Int
        get() = (tick * LEVEL_STEP) % LEVEL_RANGE

    private var nextId = INITIAL_ITEMS + 1
    private var ticker: Job? = null

    fun onToggleRunning() {
        if (running) {
            ticker?.cancel()
            running = false
        } else {
            running = true
            ticker = viewModelScope.launch {
                while (isActive) {
                    delay(tickMillis)
                    tick++
                }
            }
        }
    }

    fun onAddAtTop() {
        items.add(0, ListItem(nextId, "Item $nextId"))
        nextId++
    }

    fun onReset() {
        counters.reset()
    }

    private companion object {
        const val DEFAULT_TICK_MILLIS = 150L
        const val INITIAL_ITEMS = 5
        const val LEVEL_STEP = 7
        const val LEVEL_RANGE = 100
    }
}
