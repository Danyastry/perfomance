package com.danyastry.performance.feature.scrolling.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danyastry.performance.core.ui.components.SegmentedSelector
import com.danyastry.performance.core.ui.environment.EnvironmentBanner
import com.danyastry.performance.feature.scrolling.R
import com.danyastry.performance.feature.scrolling.jank.JankStatsEffect
import com.danyastry.performance.feature.scrolling.model.FeedItem
import com.danyastry.performance.feature.scrolling.model.JankSummary
import com.danyastry.performance.feature.scrolling.model.ListMode
import com.danyastry.performance.feature.scrolling.model.ProductRowModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScrollingRoute(modifier: Modifier = Modifier) {
    val viewModel: ScrollingViewModel = viewModel()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val models by viewModel.models.collectAsStateWithLifecycle()
    val image by viewModel.image.collectAsStateWithLifecycle()
    val summary by viewModel.counter.summary.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var autoScrolling by remember { mutableStateOf(false) }
    JankStatsEffect(active = listState.isScrollInProgress, counter = viewModel.counter)
    LaunchedEffect(autoScrolling) {
        if (autoScrolling) {
            listState.scrollToItem(0)
            listState.animateScrollBy(AUTO_SCROLL_PX, tween(AUTO_SCROLL_MILLIS, easing = LinearEasing))
            autoScrolling = false
        }
    }
    Column(modifier = modifier.semantics { testTagsAsResourceId = true }) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EnvironmentBanner()
            SegmentedSelector(
                options = ListMode.entries,
                selected = mode,
                label = { stringResource(if (it == ListMode.HEAVY) R.string.scrolling_mode_heavy else R.string.scrolling_mode_optimized) },
                onSelect = viewModel::onModeChange,
            )
            Text(
                stringResource(if (mode == ListMode.HEAVY) R.string.scrolling_heavy_explanation else R.string.scrolling_optimized_explanation),
                style = MaterialTheme.typography.bodySmall,
            )
            JankCard(summary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { autoScrolling = true }, enabled = !autoScrolling) { Text(stringResource(R.string.scrolling_auto)) }
                OutlinedButton(onClick = viewModel::onResetJank) { Text(stringResource(R.string.scrolling_reset)) }
            }
        }
        HorizontalDivider()
        Feed(mode, viewModel.items, models, image, listState, Modifier.weight(1f))
    }
}

@Composable
private fun Feed(
    mode: ListMode,
    items: List<FeedItem>,
    models: Map<Int, ProductRowModel>,
    image: ByteArray?,
    state: LazyListState,
    modifier: Modifier,
) {
    LazyColumn(state = state, modifier = modifier.fillMaxWidth().testTag(FEED_TAG)) {
        when (mode) {
            ListMode.HEAVY -> items(items) { item ->
                when (item) {
                    is FeedItem.Header -> HeaderRow(item)
                    is FeedItem.Product -> HeavyProductRow(item, image)
                }
            }
            ListMode.OPTIMIZED -> items(items, key = { it.id }, contentType = { it::class }) { item ->
                when (item) {
                    is FeedItem.Header -> HeaderRow(item)
                    is FeedItem.Product -> models[item.id]?.let { OptimizedProductRow(it, image) }
                }
            }
        }
    }
}

@Composable
private fun JankCard(summary: JankSummary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Stat(stringResource(R.string.scrolling_frames), summary.frames.toString())
            Stat(stringResource(R.string.scrolling_janky), stringResource(R.string.scrolling_janky_value, summary.jankyFrames, summary.jankyPercent))
            Stat(stringResource(R.string.scrolling_slowest), stringResource(R.string.scrolling_ms, summary.slowestFrameMillis))
        }
        Row(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Stat(stringResource(R.string.scrolling_median), stringResource(R.string.scrolling_decimal_ms, summary.medianFrameMicros / 1000.0))
            Stat(stringResource(R.string.scrolling_p90), stringResource(R.string.scrolling_decimal_ms, summary.p90FrameMicros / 1000.0))
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

const val FEED_TAG = "feed_list"
private const val AUTO_SCROLL_PX = 60_000f
private const val AUTO_SCROLL_MILLIS = 6_000
