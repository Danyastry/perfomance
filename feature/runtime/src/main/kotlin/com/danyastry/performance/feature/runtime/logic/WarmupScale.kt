package com.danyastry.performance.feature.runtime.logic

import kotlin.math.log10

object WarmupScale {

    const val MAX_SLOWDOWN = 100.0

    fun heights(batchMicros: List<Long>, warmMicros: Long): List<Float> {
        val warm = warmMicros.coerceAtLeast(1).toDouble()
        val top = log10(MAX_SLOWDOWN)
        return batchMicros.map { micros ->
            (log10((micros / warm).coerceIn(1.0, MAX_SLOWDOWN)) / top).toFloat()
        }
    }
}
