package com.danyastry.performance.catalog

import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.feature.startup.StartupFeature

object FeatureRegistry {
    val features: List<FeatureEntry> = listOf(
        StartupFeature,
    )
}
