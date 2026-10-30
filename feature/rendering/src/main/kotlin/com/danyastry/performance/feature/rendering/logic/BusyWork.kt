package com.danyastry.performance.feature.rendering.logic

object BusyWork {

    fun spin(millis: Long, nanoTime: () -> Long = System::nanoTime): Long {
        val start = nanoTime()
        val end = start + millis * NANOS_PER_MILLI
        var spins = 0L
        while (nanoTime() < end) spins++
        return spins
    }

    const val LOAD_MILLIS = 8L
    private const val NANOS_PER_MILLI = 1_000_000L
}
