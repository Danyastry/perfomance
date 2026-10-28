package com.danyastry.performance.feature.recomposition.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.danyastry.performance.feature.recomposition.model.ListItem
import com.danyastry.performance.feature.recomposition.tracking.CounterIds
import com.danyastry.performance.feature.recomposition.tracking.RecompositionCounters
import com.danyastry.performance.feature.recomposition.tracking.TrackRecompositions

@Composable
fun KeysDemo(items: List<ListItem>, counters: RecompositionCounters) {
    DemoColumns(
        problem = {
            Column {
                for (item in items) ItemRow(item, counters, CounterIds.ROWS_WITHOUT_KEYS)
            }
        },
        fixed = {
            Column {
                for (item in items) key(item.id) { ItemRow(item, counters, CounterIds.ROWS_WITH_KEYS) }
            }
        },
    )
}

@Composable
fun ItemRow(item: ListItem, counters: RecompositionCounters, id: String) {
    TrackRecompositions(counters, id)
    Text(item.label, style = MaterialTheme.typography.bodySmall)
}
