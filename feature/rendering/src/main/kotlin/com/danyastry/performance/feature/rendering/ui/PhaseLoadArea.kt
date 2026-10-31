package com.danyastry.performance.feature.rendering.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import com.danyastry.performance.feature.rendering.logic.BusyWork
import com.danyastry.performance.feature.rendering.model.PhaseLoad
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PhaseLoadArea(load: PhaseLoad, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "pipeline")
    val angle = transition.animateFloat(
        initialValue = 0f,
        targetValue = FULL_TURN,
        animationSpec = infiniteRepeatable(tween(TURN_MILLIS, easing = LinearEasing)),
        label = "angle",
    )
    val color = MaterialTheme.colorScheme.primary
    Box(modifier.fillMaxWidth().height(AREA_HEIGHT.dp).clipToBounds()) {
        when (load) {
            PhaseLoad.NONE -> Canvas(Modifier.fillMaxSize()) { spinner(angle.value, color) }
            PhaseLoad.COMPOSITION -> SlowComposition(angle.value, color)
            PhaseLoad.LAYOUT -> SlowLayout(angle, color)
            PhaseLoad.DRAW -> Canvas(Modifier.fillMaxSize()) {
                BusyWork.spin(BusyWork.LOAD_MILLIS)
                spinner(angle.value, color)
            }
            PhaseLoad.RENDER_THREAD -> {
                Canvas(Modifier.fillMaxSize().graphicsLayer { rotationZ = angle.value }) { overdraw(color) }
                Canvas(Modifier.fillMaxSize()) { spinner(angle.value, color) }
            }
        }
    }
}

@Composable
private fun SlowComposition(angle: Float, color: Color) {
    BusyWork.spin(BusyWork.LOAD_MILLIS)
    Canvas(Modifier.fillMaxSize()) { spinner(angle, color) }
}

@Composable
private fun SlowLayout(angle: State<Float>, color: Color) {
    Layout(content = {}, modifier = Modifier.fillMaxSize().drawBehind { spinner(angle.value, color) }) { _, constraints ->
        angle.value
        BusyWork.spin(BusyWork.LOAD_MILLIS)
        layout(constraints.maxWidth, constraints.maxHeight) {}
    }
}

private fun DrawScope.spinner(angle: Float, color: Color) {
    val diameter = size.minDimension * SPINNER_SHARE
    drawArc(
        color = color,
        startAngle = angle,
        sweepAngle = SPINNER_SWEEP,
        useCenter = false,
        topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2),
        size = Size(diameter, diameter),
        style = Stroke(width = diameter / 10),
    )
}

private fun DrawScope.overdraw(color: Color) {
    val radius = size.minDimension * OVERDRAW_RADIUS_SHARE
    val faint = color.copy(alpha = OVERDRAW_ALPHA)
    for (index in 0 until OVERDRAW_CIRCLES) {
        val phase = index * GOLDEN_ANGLE
        val x = size.width / 2 + (size.width / 3) * cos(phase).toFloat()
        val y = size.height / 2 + (size.height / 3) * sin(phase * 1.3).toFloat()
        drawCircle(faint, radius = radius, center = Offset(x, y))
    }
}

private const val FULL_TURN = 360f
private const val TURN_MILLIS = 1_000
private const val AREA_HEIGHT = 160
private const val SPINNER_SHARE = 0.5f
private const val SPINNER_SWEEP = 270f
private const val OVERDRAW_CIRCLES = 4_000
private const val OVERDRAW_RADIUS_SHARE = 0.6f
private const val OVERDRAW_ALPHA = 0.02f
private const val GOLDEN_ANGLE = 2.399963
