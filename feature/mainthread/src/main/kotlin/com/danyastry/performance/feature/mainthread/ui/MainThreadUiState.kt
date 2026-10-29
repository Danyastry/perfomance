package com.danyastry.performance.feature.mainthread.ui

import com.danyastry.performance.feature.mainthread.model.LoadMode
import com.danyastry.performance.feature.mainthread.model.LoadReport
import com.danyastry.performance.feature.mainthread.model.ViolationEntry

data class MainThreadUiState(
    val mode: LoadMode = LoadMode.MAIN_THREAD,
    val running: Boolean = false,
    val runs: Int = 0,
    val lastReport: LoadReport? = null,
    val error: String? = null,
    val violations: List<ViolationEntry> = emptyList(),
)
