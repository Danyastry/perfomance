package com.danyastry.performance.feature.memory.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danyastry.performance.core.ui.components.Stat
import com.danyastry.performance.core.ui.environment.EnvironmentBanner
import com.danyastry.performance.feature.memory.R
import com.danyastry.performance.feature.memory.model.MemorySnapshot
import com.danyastry.performance.feature.memory.screen.FixedActivity
import com.danyastry.performance.feature.memory.screen.LeakyActivity
import kotlinx.coroutines.delay

@Composable
fun MemoryRoute(modifier: Modifier = Modifier) {
    val viewModel: MemoryViewModel = viewModel()
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                viewModel.refresh()
                delay(REFRESH_MILLIS)
            }
        }
    }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnvironmentBanner()
        Text(stringResource(R.string.memory_intro), style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { context.open(LeakyActivity::class.java) }) { Text(stringResource(R.string.memory_open_leaky)) }
            Button(onClick = { context.open(FixedActivity::class.java) }) { Text(stringResource(R.string.memory_open_fixed)) }
        }
        SnapshotCard(snapshot)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = viewModel::onCollectGarbage) { Text(stringResource(R.string.memory_gc)) }
            OutlinedButton(onClick = viewModel::onClearListeners) { Text(stringResource(R.string.memory_clear)) }
        }
        Text(stringResource(R.string.memory_how_to_read), style = MaterialTheme.typography.bodySmall)
        if (snapshot.leakCanaryAvailable) {
            Text(stringResource(R.string.memory_leakcanary_note), style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = viewModel::onDumpHeap) { Text(stringResource(R.string.memory_dump)) }
        }
    }
}

@Composable
private fun SnapshotCard(snapshot: MemorySnapshot) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Stat(stringResource(R.string.memory_listeners), snapshot.listeners.toString())
                Stat(
                    stringResource(R.string.memory_heap),
                    stringResource(R.string.memory_heap_value, snapshot.heap.usedMegabytes, snapshot.heap.maxMegabytes),
                )
            }
            Stat(
                stringResource(R.string.memory_retained),
                if (snapshot.leakCanaryAvailable) snapshot.retainedObjects.toString() else stringResource(R.string.memory_retained_unavailable),
            )
        }
    }
}

private fun Context.open(activity: Class<*>) = startActivity(Intent(this, activity))

private const val REFRESH_MILLIS = 1_000L
