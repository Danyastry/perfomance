package com.danyastry.performance.feature.recomposition.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.danyastry.performance.feature.recomposition.model.Labels
import com.danyastry.performance.feature.recomposition.model.Person
import com.danyastry.performance.feature.recomposition.tracking.CounterIds
import com.danyastry.performance.feature.recomposition.tracking.RecompositionCounters
import com.danyastry.performance.feature.recomposition.tracking.TrackRecompositions

@Composable
fun StabilityDemo(tick: Int, counters: RecompositionCounters) {
    Text("Parent tick $tick", style = MaterialTheme.typography.bodySmall)
    val names = SOURCE.map { it.name }
    DemoColumns(
        problem = { UnstableListChild(names, counters) },
        fixed = { ImmutableClassChild(Labels(names), counters) },
    )
}

@Composable
fun UnstableListChild(names: List<String>, counters: RecompositionCounters) {
    TrackRecompositions(counters, CounterIds.UNSTABLE_LIST)
    Text(names.joinToString(), style = MaterialTheme.typography.bodySmall)
}

@Composable
fun ImmutableClassChild(labels: Labels, counters: RecompositionCounters) {
    TrackRecompositions(counters, CounterIds.IMMUTABLE_CLASS)
    Text(labels.values.joinToString(), style = MaterialTheme.typography.bodySmall)
}

private val SOURCE = listOf(Person("alpha"), Person("beta"), Person("gamma"))
