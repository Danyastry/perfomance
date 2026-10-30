package com.danyastry.performance.feature.startup.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.danyastry.performance.core.contract.LabTags
import com.danyastry.performance.core.ui.components.SegmentedSelector
import com.danyastry.performance.core.ui.environment.EnvironmentBanner
import com.danyastry.performance.feature.startup.R
import com.danyastry.performance.feature.startup.data.PreferencesStartupStore
import com.danyastry.performance.feature.startup.data.ProfileStatusReader
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.ModeStats
import com.danyastry.performance.feature.startup.model.ProfileStatus
import com.danyastry.performance.feature.startup.model.StartupSample
import com.danyastry.performance.feature.startup.restart.RestartActivity
import com.danyastry.performance.feature.startup.tracking.StartupTracker

@Composable
fun StartupRoute(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: StartupViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val reader = ProfileStatusReader(context)
                StartupViewModel(
                    store = PreferencesStartupStore(context),
                    lastLaunch = StartupTracker.lastLaunch,
                    library = StartupLibrary.heavy,
                    profileStatus = reader::read,
                )
            }
        },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    StartupScreen(
        state = state,
        actions = StartupActions(
            onModeChange = viewModel::onModeChange,
            onRestart = { RestartActivity.coldRestart(context) },
            onUseLibrary = viewModel::onUseLibrary,
            onClearHistory = viewModel::onClearHistory,
        ),
        modifier = modifier,
    )
}

@Composable
fun StartupScreen(state: StartupUiState, actions: StartupActions, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { EnvironmentBanner() }
        item { LastLaunchCard(state.lastLaunch) }
        item { ModePicker(state, actions) }
        item { LibraryCard(state, actions) }
        state.profileStatus?.let { status -> item { ProfileCard(status) } }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.startup_history), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                TextButton(onClick = actions.onClearHistory, enabled = state.stats.isNotEmpty()) { Text(stringResource(R.string.startup_clear)) }
            }
        }
        if (state.stats.isEmpty()) {
            item { Text(stringResource(R.string.startup_history_empty), style = MaterialTheme.typography.bodySmall) }
        }
        items(state.stats, key = { it.mode }) { StatsRow(it) }
    }
}

@Composable
private fun LastLaunchCard(sample: StartupSample?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringResource(R.string.startup_last_launch), style = MaterialTheme.typography.labelMedium)
            if (sample == null) {
                Text(stringResource(R.string.startup_no_launch), style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(stringResource(R.string.startup_first_frame, sample.firstFrameMillis), style = MaterialTheme.typography.headlineSmall)
                Text(stringResource(R.string.startup_application, sample.applicationMillis), style = MaterialTheme.typography.bodyMedium)
                Text(stringResource(R.string.startup_launched_with, stringResource(sample.mode.labelRes)), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ModePicker(state: StartupUiState, actions: StartupActions) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.startup_mode_title), style = MaterialTheme.typography.titleSmall)
        SegmentedSelector(
            options = InitMode.entries,
            selected = state.selectedMode,
            label = { stringResource(it.labelRes) },
            onSelect = actions.onModeChange,
        )
        Text(stringResource(state.selectedMode.explanationRes), style = MaterialTheme.typography.bodySmall)
        Button(onClick = actions.onRestart, modifier = Modifier.fillMaxWidth().testTag(LabTags.COLD_START_RESTART)) {
            Text(stringResource(if (state.restartNeeded) R.string.startup_restart_to_apply else R.string.startup_restart))
        }
    }
}

@Composable
private fun LibraryCard(state: StartupUiState, actions: StartupActions) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.startup_library_title), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(if (state.libraryReady) R.string.startup_library_ready else R.string.startup_library_not_ready),
                style = MaterialTheme.typography.bodyMedium,
            )
            state.firstUseMillis?.let {
                Text(
                    stringResource(if (it == 0L) R.string.startup_first_use_free else R.string.startup_first_use_cost, it),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            OutlinedButton(onClick = actions.onUseLibrary, enabled = !state.usingLibrary) { Text(stringResource(R.string.startup_use_library)) }
        }
    }
}

@Composable
private fun ProfileCard(status: ProfileStatus) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.startup_profile_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(status.labelRes), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(stringResource(status.explanationRes), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun StatsRow(stats: ModeStats) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(stats.mode.labelRes), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.startup_stats, stats.medianFirstFrameMillis, stats.bestFirstFrameMillis, stats.launches), style = MaterialTheme.typography.bodySmall)
    }
}
