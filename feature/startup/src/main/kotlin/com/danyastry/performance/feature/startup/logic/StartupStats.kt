package com.danyastry.performance.feature.startup.logic

import com.danyastry.performance.core.ui.stats.Median
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.ModeStats
import com.danyastry.performance.feature.startup.model.StartupSample

object StartupStats {

    fun byMode(samples: List<StartupSample>): List<ModeStats> = InitMode.entries.mapNotNull { mode ->
        val times = samples.filter { it.mode == mode }.map { it.firstFrameMillis }.sorted()
        if (times.isEmpty()) {
            null
        } else {
            ModeStats(mode, times.size, Median.of(times), times.first())
        }
    }
}
