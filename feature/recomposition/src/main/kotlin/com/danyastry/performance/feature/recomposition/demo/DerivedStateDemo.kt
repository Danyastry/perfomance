package com.danyastry.performance.feature.recomposition.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.danyastry.performance.feature.recomposition.tracking.CounterIds
import com.danyastry.performance.feature.recomposition.tracking.RecompositionCounters
import com.danyastry.performance.feature.recomposition.tracking.TrackRecompositions

@Composable
fun DerivedStateDemo(level: () -> Int, counters: RecompositionCounters) {
    DemoColumns(
        problem = { PlainConditionChild(level, counters) },
        fixed = { DerivedConditionChild(level, counters) },
    )
}

@Composable
fun PlainConditionChild(level: () -> Int, counters: RecompositionCounters) {
    TrackRecompositions(counters, CounterIds.PLAIN_CONDITION)
    val high = level() > THRESHOLD
    Text(if (high) "Above $THRESHOLD" else "At or below $THRESHOLD", style = MaterialTheme.typography.bodySmall)
}

@Composable
fun DerivedConditionChild(level: () -> Int, counters: RecompositionCounters) {
    TrackRecompositions(counters, CounterIds.DERIVED_CONDITION)
    val high by remember { derivedStateOf { level() > THRESHOLD } }
    Text(if (high) "Above $THRESHOLD" else "At or below $THRESHOLD", style = MaterialTheme.typography.bodySmall)
}

const val THRESHOLD = 50
