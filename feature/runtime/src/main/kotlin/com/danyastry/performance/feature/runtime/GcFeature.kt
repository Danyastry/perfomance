package com.danyastry.performance.feature.runtime

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.runtime.ui.GcRoute

object GcFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = "garbage-collection",
        name = "Garbage collection",
        category = FeatureCategory.UNDER_THE_HOOD,
        summary = "An animation that allocates every frame next to one that reuses arrays, with live ART GC counters",
    )

    @Composable
    override fun Content(modifier: Modifier) {
        GcRoute(modifier = modifier)
    }
}
