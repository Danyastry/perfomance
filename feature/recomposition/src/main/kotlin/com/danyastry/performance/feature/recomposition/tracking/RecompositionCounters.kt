package com.danyastry.performance.feature.recomposition.tracking

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecompositionCounters {

    private val _counts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val counts: StateFlow<Map<String, Int>> = _counts.asStateFlow()

    fun increment(id: String) {
        _counts.update { it + (id to (it[id] ?: 0) + 1) }
    }

    fun count(id: String): Int = _counts.value[id] ?: 0

    fun reset() {
        _counts.value = emptyMap()
    }
}
