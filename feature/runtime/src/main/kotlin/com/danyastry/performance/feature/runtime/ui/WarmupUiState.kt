package com.danyastry.performance.feature.runtime.ui

import com.danyastry.performance.feature.runtime.model.WarmupRun
import com.danyastry.performance.feature.runtime.model.WarmupSummary

data class WarmupUiState(
    val running: Boolean = false,
    val run: WarmupRun? = null,
    val summary: WarmupSummary? = null,
)
