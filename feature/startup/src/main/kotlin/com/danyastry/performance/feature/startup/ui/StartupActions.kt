package com.danyastry.performance.feature.startup.ui

import com.danyastry.performance.feature.startup.model.InitMode

data class StartupActions(
    val onModeChange: (InitMode) -> Unit,
    val onRestart: () -> Unit,
    val onUseLibrary: () -> Unit,
    val onClearHistory: () -> Unit,
)
