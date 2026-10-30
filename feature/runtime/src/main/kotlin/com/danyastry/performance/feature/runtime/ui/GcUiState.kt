package com.danyastry.performance.feature.runtime.ui

import com.danyastry.performance.feature.runtime.model.GcCounters
import com.danyastry.performance.feature.runtime.model.GcMode
import com.danyastry.performance.feature.runtime.model.GcReport

data class GcUiState(
    val mode: GcMode = GcMode.ALLOCATE,
    val running: Boolean = false,
    val live: GcCounters = GcCounters.ZERO,
    val report: GcReport? = null,
)
