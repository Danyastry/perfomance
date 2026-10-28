package com.danyastry.performance.feature.recomposition.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.danyastry.performance.feature.recomposition.tracking.CounterIds
import com.danyastry.performance.feature.recomposition.tracking.RecompositionCounters
import com.danyastry.performance.feature.recomposition.tracking.TrackRecompositions

@Composable
fun DeferredReadDemo(tick: () -> Int, counters: RecompositionCounters) {
    DemoColumns(
        problem = { EarlyReadChild(tick, counters) },
        fixed = { LateReadChild(tick, counters) },
    )
}

@Composable
fun EarlyReadChild(tick: () -> Int, counters: RecompositionCounters) {
    TrackRecompositions(counters, CounterIds.EARLY_READ)
    val x = offsetFor(tick())
    Box(Modifier.offset(x = x.dp).size(DOT_SIZE.dp).background(MaterialTheme.colorScheme.error))
}

@Composable
fun LateReadChild(tick: () -> Int, counters: RecompositionCounters) {
    TrackRecompositions(counters, CounterIds.LATE_READ)
    val density = LocalDensity.current.density
    Box(
        Modifier
            .offset { IntOffset((offsetFor(tick()) * density).toInt(), 0) }
            .size(DOT_SIZE.dp)
            .background(MaterialTheme.colorScheme.primary),
    )
}

internal fun offsetFor(tick: Int): Int = (tick % STEPS) * STEP_DP

private const val STEPS = 12
private const val STEP_DP = 8
private const val DOT_SIZE = 16
