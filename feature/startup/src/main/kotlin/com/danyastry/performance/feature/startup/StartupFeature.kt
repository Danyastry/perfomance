package com.danyastry.performance.feature.startup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.startup.ui.StartupRoute

object StartupFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = "cold-start",
        name = "Cold start",
        category = FeatureCategory.STARTUP,
        summary = "Measure real cold launches and move a heavy SDK from startup to lazy or background init",
    )

    @Composable
    override fun Content(modifier: Modifier) {
        StartupRoute(modifier = modifier)
    }
}
