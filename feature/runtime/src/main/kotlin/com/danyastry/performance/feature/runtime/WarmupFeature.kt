package com.danyastry.performance.feature.runtime

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.runtime.ui.WarmupRoute

object WarmupFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = "jit-warmup",
        name = "JIT warm-up",
        category = FeatureCategory.UNDER_THE_HOOD,
        summary = "Watch ART interpret, JIT-compile and speed up the same loop, and compare with ahead-of-time compilation",
    )

    @Composable
    override fun Content(modifier: Modifier) {
        WarmupRoute(modifier = modifier)
    }
}
