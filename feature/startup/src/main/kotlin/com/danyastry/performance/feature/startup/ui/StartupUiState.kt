package com.danyastry.performance.feature.startup.ui

import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.ModeStats
import com.danyastry.performance.feature.startup.model.StartupSample

data class StartupUiState(
    val selectedMode: InitMode = InitMode.EAGER,
    val launchedWith: InitMode? = null,
    val lastLaunch: StartupSample? = null,
    val stats: List<ModeStats> = emptyList(),
    val libraryReady: Boolean = false,
    val firstUseMillis: Long? = null,
    val usingLibrary: Boolean = false,
) {
    val restartNeeded: Boolean
        get() = launchedWith != null && launchedWith != selectedMode
}
