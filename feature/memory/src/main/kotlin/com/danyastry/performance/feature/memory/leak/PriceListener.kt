package com.danyastry.performance.feature.memory.leak

fun interface PriceListener {
    fun onPrice(cents: Long)
}
