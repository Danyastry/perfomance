package com.danyastry.performance.feature.runtime.ui

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.danyastry.performance.feature.runtime.logic.WarmupScale

@Composable
fun WarmupChart(batchMicros: List<Long>, warmMicros: Long, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primary
    val baseline = MaterialTheme.colorScheme.outline
    val heights = remember(batchMicros, warmMicros) { WarmupScale.heights(batchMicros, warmMicros) }
    Canvas(modifier) {
        if (heights.isEmpty()) return@Canvas
        val barWidth = size.width / heights.size
        heights.forEachIndexed { index, share ->
            val height = (share * size.height).coerceAtLeast(MIN_BAR)
            drawRect(color, topLeft = Offset(index * barWidth, size.height - height), size = Size(barWidth.coerceAtLeast(1f), height))
        }
        drawLine(baseline, Offset(0f, size.height), Offset(size.width, size.height))
    }
}

private const val MIN_BAR = 1f
