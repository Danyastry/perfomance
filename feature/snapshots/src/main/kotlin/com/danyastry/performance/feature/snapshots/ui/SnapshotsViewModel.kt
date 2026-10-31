package com.danyastry.performance.feature.snapshots.ui

import androidx.lifecycle.ViewModel
import com.danyastry.performance.feature.snapshots.logic.SnapshotDemos
import com.danyastry.performance.feature.snapshots.model.DemoStep
import com.danyastry.performance.feature.snapshots.model.SnapshotDemo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SnapshotsViewModel(
    private val runDemo: (SnapshotDemo) -> List<DemoStep> = SnapshotDemos::run,
) : ViewModel() {

    private val _results = MutableStateFlow<Map<SnapshotDemo, List<DemoStep>>>(emptyMap())
    val results: StateFlow<Map<SnapshotDemo, List<DemoStep>>> = _results.asStateFlow()

    fun onRun(demo: SnapshotDemo) {
        val steps = runDemo(demo)
        _results.update { it + (demo to steps) }
    }
}
