package com.danyastry.performance.feature.memory.logic

import kotlin.random.Random

object PriceTicker {
    private const val MIN_CENTS = 10_000L
    private const val MAX_CENTS = 99_999L

    fun next(random: Random = Random.Default): Long = random.nextLong(MIN_CENTS, MAX_CENTS + 1)
}
