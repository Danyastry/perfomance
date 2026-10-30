package com.danyastry.performance.feature.rendering.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danyastry.performance.core.ui.environment.EnvironmentBanner
import com.danyastry.performance.feature.rendering.R
import com.danyastry.performance.feature.rendering.data.FrameMetricsRecorder
import com.danyastry.performance.feature.rendering.logic.BusyWork
import com.danyastry.performance.feature.rendering.model.FramePhase
import com.danyastry.performance.feature.rendering.model.PhaseLoad
import com.danyastry.performance.feature.rendering.model.PipelineSnapshot

@Composable
fun PipelineRoute(modifier: Modifier = Modifier) {
    val viewModel: PipelineViewModel = viewModel()
    val load by viewModel.load.collectAsStateWithLifecycle()
    val snapshot by viewModel.snapshots.collectAsStateWithLifecycle(PipelineSnapshot.EMPTY)
    val window = LocalContext.current.findActivity()?.window
    val refreshRate = LocalView.current.display?.refreshRate ?: DEFAULT_REFRESH_RATE
    DisposableEffect(window, viewModel) {
        val recorder = FrameMetricsRecorder(viewModel.history)
        window?.let(recorder::attach)
        onDispose { recorder.detach() }
    }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnvironmentBanner()
        Text(stringResource(R.string.rendering_intro), style = MaterialTheme.typography.bodyMedium)
        LoadPicker(load, viewModel::onLoadChange)
        Text(stringResource(PipelineLabels.loadExplanation(load), BusyWork.LOAD_MILLIS), style = MaterialTheme.typography.bodySmall)
        PhaseLoadArea(load)
        BreakdownCard(snapshot, MILLIS_PER_SECOND / refreshRate)
        Text(stringResource(R.string.rendering_how_to_read), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun LoadPicker(selected: PhaseLoad, onSelect: (PhaseLoad) -> Unit) {
    Column {
        PhaseLoad.entries.forEach { load ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().selectable(selected = load == selected, role = Role.RadioButton) { onSelect(load) },
            ) {
                RadioButton(selected = load == selected, onClick = null)
                Text(stringResource(PipelineLabels.load(load)), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun BreakdownCard(snapshot: PipelineSnapshot, frameBudgetMillis: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.rendering_chart_title, frameBudgetMillis), style = MaterialTheme.typography.titleSmall)
            PipelineChart(snapshot.recentMillis, frameBudgetMillis, Modifier.fillMaxWidth().height(140.dp))
            Text(stringResource(R.string.rendering_average_title, snapshot.frames), style = MaterialTheme.typography.titleSmall)
            FramePhase.entries.forEach { phase ->
                if (phase != FramePhase.GPU || snapshot.gpuAvailable) PhaseRow(phase, snapshot.averageMillis[phase] ?: 0.0)
            }
            if (!snapshot.gpuAvailable) Text(stringResource(R.string.rendering_gpu_unavailable), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun PhaseRow(phase: FramePhase, millis: Double) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(12.dp).background(PhaseColors.of(phase)))
        Text(stringResource(PipelineLabels.phase(phase)), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(stringResource(PipelineLabels.thread(phase.thread)), style = MaterialTheme.typography.labelSmall)
        Text(stringResource(R.string.rendering_ms, millis), style = MaterialTheme.typography.bodyMedium)
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val DEFAULT_REFRESH_RATE = 60f
private const val MILLIS_PER_SECOND = 1_000.0
