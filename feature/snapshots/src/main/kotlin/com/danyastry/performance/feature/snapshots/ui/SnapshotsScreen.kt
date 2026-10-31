package com.danyastry.performance.feature.snapshots.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danyastry.performance.feature.snapshots.R
import com.danyastry.performance.feature.snapshots.model.DemoStep
import com.danyastry.performance.feature.snapshots.model.SnapshotDemo

@Composable
fun SnapshotsRoute(modifier: Modifier = Modifier) {
    val viewModel: SnapshotsViewModel = viewModel()
    val results by viewModel.results.collectAsStateWithLifecycle()
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.snapshots_intro), style = MaterialTheme.typography.bodyMedium)
        SnapshotDemo.entries.forEach { demo ->
            DemoCard(demo, results[demo], onRun = { viewModel.onRun(demo) })
        }
        Text(stringResource(R.string.snapshots_why_it_matters), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun DemoCard(demo: SnapshotDemo, steps: List<DemoStep>?, onRun: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(SnapshotLabels.title(demo)), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(SnapshotLabels.explanation(demo)), style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = onRun) {
                Text(stringResource(if (steps == null) R.string.snapshots_run else R.string.snapshots_run_again))
            }
            if (steps != null) Transcript(steps)
        }
    }
}

@Composable
private fun Transcript(steps: List<DemoStep>) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            steps.forEach { step ->
                Text(step.code, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                if (step.observed.isNotEmpty()) {
                    Text(
                        stringResource(R.string.snapshots_observed, step.observed),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
