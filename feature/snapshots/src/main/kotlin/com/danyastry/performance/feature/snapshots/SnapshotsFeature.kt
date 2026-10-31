package com.danyastry.performance.feature.snapshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.contract.LabIds
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.snapshots.ui.SnapshotsRoute

object SnapshotsFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = LabIds.COMPOSE_SNAPSHOTS,
        name = "Compose snapshots",
        category = FeatureCategory.UNDER_THE_HOOD,
        summary = "Isolation, apply conflicts and read observation, run live on the real snapshot system",
    )

    @Composable
    override fun Content(modifier: Modifier) {
        SnapshotsRoute(modifier = modifier)
    }
}
