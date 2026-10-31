package com.danyastry.performance.catalog

import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.feature.mainthread.MainThreadFeature
import com.danyastry.performance.feature.memory.MemoryFeature
import com.danyastry.performance.feature.recomposition.RecompositionFeature
import com.danyastry.performance.feature.rendering.PipelineFeature
import com.danyastry.performance.feature.runtime.GcFeature
import com.danyastry.performance.feature.runtime.WarmupFeature
import com.danyastry.performance.feature.scrolling.ScrollingFeature
import com.danyastry.performance.feature.snapshots.SnapshotsFeature
import com.danyastry.performance.feature.startup.StartupFeature

object FeatureRegistry {
    val features: List<FeatureEntry> = listOf(
        StartupFeature,
        RecompositionFeature,
        ScrollingFeature,
        MemoryFeature,
        MainThreadFeature,
        WarmupFeature,
        GcFeature,
        PipelineFeature,
        SnapshotsFeature,
    )
}
