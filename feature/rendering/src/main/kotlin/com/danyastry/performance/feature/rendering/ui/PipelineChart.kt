package com.danyastry.performance.feature.rendering.ui

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import com.danyastry.performance.feature.rendering.model.FramePhase
import com.danyastry.performance.feature.rendering.model.PipelineSnapshot

@Composable
fun PipelineChart(snapshot: PipelineSnapshot, frameBudgetMillis: Double, modifier: Modifier = Modifier) {
    val budgetColor = MaterialTheme.colorScheme.error
    val stacked = FramePhase.entries.filter { it.stacked }
    Canvas(modifier) {
        val scaleMillis = frameBudgetMillis * SCALE_BUDGETS
        val slot = size.width / SLOTS
        for (index in 0 until snapshot.recentCount) {
            var top = size.height
            stacked.forEach { phase ->
                val height = (snapshot.recent(index, phase) / scaleMillis * size.height).toFloat()
                if (height > 0f) {
                    top -= height
                    drawRect(PhaseColors.of(phase), topLeft = Offset(index * slot, top.coerceAtLeast(0f)), size = Size(slot * BAR_SHARE, height))
                }
            }
        }
        val budgetY = (size.height - frameBudgetMillis / scaleMillis * size.height).toFloat()
        drawLine(budgetColor, Offset(0f, budgetY), Offset(size.width, budgetY), strokeWidth = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)))
    }
}

private const val SLOTS = 60
private const val SCALE_BUDGETS = 2.0
private const val BAR_SHARE = 0.8f
