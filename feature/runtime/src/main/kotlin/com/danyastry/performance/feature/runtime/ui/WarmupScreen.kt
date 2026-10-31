package com.danyastry.performance.feature.runtime.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danyastry.performance.core.ui.components.Stat
import com.danyastry.performance.core.ui.environment.EnvironmentBanner
import com.danyastry.performance.feature.runtime.R
import com.danyastry.performance.feature.runtime.logic.WarmupRunner
import com.danyastry.performance.feature.runtime.logic.WarmupScale
import com.danyastry.performance.feature.runtime.logic.WarmupStats
import com.danyastry.performance.feature.runtime.model.WarmupRun
import com.danyastry.performance.feature.runtime.model.WarmupSummary

@Composable
fun WarmupRoute(modifier: Modifier = Modifier) {
    val viewModel: WarmupViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnvironmentBanner()
        Text(
            stringResource(R.string.runtime_warmup_intro, WarmupRunner.BATCHES, WarmupRunner.INPUT_BYTES / 1024),
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = viewModel::onRun, enabled = !state.running) { Text(stringResource(R.string.runtime_warmup_run)) }
        val run = state.run
        val summary = state.summary
        if (run != null && summary != null) ResultCard(run, summary)
        Text(stringResource(R.string.runtime_warmup_modes_title), style = MaterialTheme.typography.titleSmall)
        Text(stringResource(R.string.runtime_warmup_modes), style = MaterialTheme.typography.bodySmall)
        SelectionContainer {
            Text(stringResource(R.string.runtime_warmup_commands), style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun ResultCard(run: WarmupRun, summary: WarmupSummary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(if (run.firstInProcess) R.string.runtime_warmup_first else R.string.runtime_warmup_again),
                style = MaterialTheme.typography.titleSmall,
            )
            WarmupChart(run.batchMicros, summary.warmMicros, Modifier.fillMaxWidth().height(120.dp))
            Text(stringResource(R.string.runtime_warmup_chart_caption, WarmupScale.MAX_SLOWDOWN.toInt()), style = MaterialTheme.typography.labelSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Stat(stringResource(R.string.runtime_warmup_first_batch), millis(summary.firstMicros))
                Stat(stringResource(R.string.runtime_warmup_warm_batch, WarmupStats.TAIL), millis(summary.warmMicros))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Stat(stringResource(R.string.runtime_warmup_speedup), stringResource(R.string.runtime_times, summary.speedup))
                Stat(stringResource(R.string.runtime_warmup_warm_from), summary.warmFromBatch.toString())
            }
        }
    }
}

@Composable
private fun millis(micros: Long): String = stringResource(R.string.runtime_ms_precise, micros / 1000.0)
