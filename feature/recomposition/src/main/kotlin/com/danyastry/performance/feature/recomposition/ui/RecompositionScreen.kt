package com.danyastry.performance.feature.recomposition.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danyastry.performance.core.ui.environment.EnvironmentBanner
import com.danyastry.performance.feature.recomposition.R
import com.danyastry.performance.feature.recomposition.demo.DeferredReadDemo
import com.danyastry.performance.feature.recomposition.demo.DerivedStateDemo
import com.danyastry.performance.feature.recomposition.demo.KeysDemo
import com.danyastry.performance.feature.recomposition.demo.StabilityDemo
import com.danyastry.performance.feature.recomposition.tracking.CounterIds
import com.danyastry.performance.feature.recomposition.tracking.RecompositionCounters

@Composable
fun RecompositionRoute(modifier: Modifier = Modifier) {
    val viewModel: RecompositionViewModel = viewModel()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { EnvironmentBanner() }
        item { Text(stringResource(R.string.recomposition_intro), style = MaterialTheme.typography.bodySmall) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::onToggleRunning) {
                    Text(stringResource(if (viewModel.running) R.string.recomposition_pause else R.string.recomposition_run))
                }
                OutlinedButton(onClick = viewModel::onReset) { Text(stringResource(R.string.recomposition_reset)) }
            }
        }
        item {
            DemoCard(R.string.recomposition_stability_title, R.string.recomposition_stability_explanation, viewModel.counters,
                CounterIds.UNSTABLE_LIST, CounterIds.IMMUTABLE_CLASS) {
                StabilityDemo(viewModel.tick, viewModel.counters)
            }
        }
        item {
            DemoCard(R.string.recomposition_read_title, R.string.recomposition_read_explanation, viewModel.counters,
                CounterIds.EARLY_READ, CounterIds.LATE_READ) {
                DeferredReadDemo({ viewModel.tick }, viewModel.counters)
            }
        }
        item {
            DemoCard(R.string.recomposition_derived_title, R.string.recomposition_derived_explanation, viewModel.counters,
                CounterIds.PLAIN_CONDITION, CounterIds.DERIVED_CONDITION) {
                DerivedStateDemo({ viewModel.level }, viewModel.counters)
            }
        }
        item {
            DemoCard(R.string.recomposition_keys_title, R.string.recomposition_keys_explanation, viewModel.counters,
                CounterIds.ROWS_WITHOUT_KEYS, CounterIds.ROWS_WITH_KEYS) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = viewModel::onAddAtTop) { Text(stringResource(R.string.recomposition_add_top)) }
                    KeysDemo(viewModel.items, viewModel.counters)
                }
            }
        }
    }
}

@Composable
private fun DemoCard(
    title: Int,
    explanation: Int,
    counters: RecompositionCounters,
    problemId: String,
    fixedId: String,
    content: @Composable () -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(explanation), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
            Score(counters, problemId, fixedId)
        }
    }
}

@Composable
private fun Score(counters: RecompositionCounters, problemId: String, fixedId: String) {
    val counts by counters.counts.collectAsStateWithLifecycle()
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            stringResource(R.string.recomposition_count, counts[problemId] ?: 0),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            stringResource(R.string.recomposition_count, counts[fixedId] ?: 0),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
