package com.danyastry.performance.feature.memory

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.memory.ui.MemoryRoute

object MemoryFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = "memory-leaks",
        name = "Memory leaks",
        category = FeatureCategory.MEMORY,
        summary = "An Activity leaked through a singleton listener next to the lifecycle-aware fix, watched by LeakCanary",
    )

    @Composable
    override fun Content(modifier: Modifier) {
        MemoryRoute(modifier = modifier)
    }
}
