package com.danyastry.performance.feature.scrolling

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.scrolling.ui.ScrollingRoute

object ScrollingFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = "list-scrolling",
        name = "List scrolling",
        category = FeatureCategory.SCROLLING,
        summary = "A heavy and an optimized feed of 1,000 rows, with a live jank meter",
    )

    @Composable
    override fun Content(modifier: Modifier) {
        ScrollingRoute(modifier = modifier)
    }
}
