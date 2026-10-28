package com.danyastry.performance.feature.recomposition

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.recomposition.ui.RecompositionRoute

object RecompositionFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = "recomposition",
        name = "Recomposition",
        category = FeatureCategory.COMPOSE,
        summary = "Four common causes of extra recomposition, each with a fix, counted live",
    )

    @Composable
    override fun Content(modifier: Modifier) {
        RecompositionRoute(modifier = modifier)
    }
}
