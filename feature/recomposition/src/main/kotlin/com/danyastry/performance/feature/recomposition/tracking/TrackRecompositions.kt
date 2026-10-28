package com.danyastry.performance.feature.recomposition.tracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun TrackRecompositions(counters: RecompositionCounters, id: String) {
    SideEffect { counters.increment(id) }
}
