package com.danyastry.performance.feature.rendering

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.contract.LabIds
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.rendering.ui.PipelineRoute

object PipelineFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = LabIds.FRAME_PIPELINE,
        name = "Frame pipeline",
        category = FeatureCategory.UNDER_THE_HOOD,
        summary = "Every frame split into its phases with FrameMetrics, and a load you can move from phase to phase",
    )

    @Composable
    override fun Content(modifier: Modifier) {
        PipelineRoute(modifier = modifier)
    }
}
