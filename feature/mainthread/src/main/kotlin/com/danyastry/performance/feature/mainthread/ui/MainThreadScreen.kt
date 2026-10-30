package com.danyastry.performance.feature.mainthread.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danyastry.performance.core.ui.components.SegmentedSelector
import com.danyastry.performance.core.ui.environment.EnvironmentBanner
import com.danyastry.performance.feature.mainthread.R
import com.danyastry.performance.feature.mainthread.logic.FrameGapStats
import com.danyastry.performance.feature.mainthread.logic.ReceiptLoader
import com.danyastry.performance.feature.mainthread.logic.ViolationGroups
import com.danyastry.performance.feature.mainthread.model.LoadMode
import com.danyastry.performance.feature.mainthread.model.LoadReport
import com.danyastry.performance.feature.mainthread.model.ParserKind
import com.danyastry.performance.feature.mainthread.model.ViolationEntry
import com.danyastry.performance.feature.mainthread.strict.StrictModeWatch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainThreadRoute(modifier: Modifier = Modifier) {
    val viewModel: MainThreadViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view = LocalView.current
    val watch = remember(viewModel) { StrictModeWatch(viewModel::onViolation) }
    DisposableEffect(watch) {
        watch.start()
        onDispose { watch.stop() }
    }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnvironmentBanner()
        Text(stringResource(R.string.mainthread_intro), style = MaterialTheme.typography.bodyMedium)
        SegmentedSelector(
            options = LoadMode.entries,
            selected = state.mode,
            label = { stringResource(MainThreadLabels.mode(it)) },
            onSelect = viewModel::onModeChange,
        )
        Text(stringResource(MainThreadLabels.explanation(state.mode)), style = MaterialTheme.typography.bodySmall)
        SegmentedSelector(
            options = ParserKind.entries,
            selected = state.parser,
            label = { stringResource(MainThreadLabels.parser(it)) },
            onSelect = viewModel::onParserChange,
        )
        Text(stringResource(MainThreadLabels.parserExplanation(state.parser)), style = MaterialTheme.typography.bodySmall)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    val interval = FrameGapStats.intervalNanos(view.display?.refreshRate ?: 0f)
                    viewModel.onLoad({ context.cacheDir }, interval)
                },
                enabled = !state.running,
            ) { Text(stringResource(R.string.mainthread_load)) }
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
            Text(stringResource(R.string.mainthread_spinner_hint), style = MaterialTheme.typography.labelSmall)
        }
        state.error?.let { Text(stringResource(R.string.mainthread_error, it), color = MaterialTheme.colorScheme.error) }
        state.lastReport?.let { ReportCard(it) }
        ViolationsCard(state.violations, viewModel::onClearViolations)
        Text(stringResource(R.string.mainthread_trace_hint, ReceiptLoader.SECTION_LOAD), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ReportCard(report: LoadReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(
                    R.string.mainthread_report_title,
                    stringResource(MainThreadLabels.mode(report.mode)),
                    stringResource(MainThreadLabels.parser(report.parser)),
                ),
                style = MaterialTheme.typography.titleSmall,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Stat(stringResource(R.string.mainthread_work), stringResource(R.string.mainthread_ms, report.workMillis))
                Stat(stringResource(R.string.mainthread_longest_gap), stringResource(R.string.mainthread_ms, report.frames.longestGapMillis))
                Stat(stringResource(R.string.mainthread_missed), report.frames.missedFrames.toString())
            }
            Text(
                stringResource(
                    R.string.mainthread_receipt,
                    report.summary.lines,
                    report.summary.distinctSkus,
                    report.summary.totalCents / 100.0,
                    report.summary.checksum.take(CHECKSUM_PREVIEW),
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ViolationsCard(violations: List<ViolationEntry>, onClear: () -> Unit) {
    val groups = remember(violations) { ViolationGroups.of(violations) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.mainthread_violations_title, violations.size), style = MaterialTheme.typography.titleSmall)
            if (violations.isEmpty()) {
                Text(stringResource(R.string.mainthread_violations_none), style = MaterialTheme.typography.bodySmall)
            } else {
                groups.forEach { group ->
                    Text(
                        stringResource(
                            R.string.mainthread_violation_line,
                            stringResource(MainThreadLabels.violation(group.kind)),
                            group.origin ?: stringResource(R.string.mainthread_origin_unknown),
                            group.thread,
                            group.count,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(stringResource(R.string.mainthread_violations_cap), style = MaterialTheme.typography.labelSmall)
                OutlinedButton(onClick = onClear) { Text(stringResource(R.string.mainthread_clear)) }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

private const val CHECKSUM_PREVIEW = 12
