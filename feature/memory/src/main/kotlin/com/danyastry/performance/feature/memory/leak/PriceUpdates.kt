package com.danyastry.performance.feature.memory.leak

import java.util.concurrent.CopyOnWriteArrayList

object PriceUpdates {

    private val listeners = CopyOnWriteArrayList<PriceListener>()

    val listenerCount: Int
        get() = listeners.size

    fun register(listener: PriceListener) {
        listeners += listener
    }

    fun unregister(listener: PriceListener) {
        listeners -= listener
    }

    fun publish(cents: Long) = listeners.forEach { it.onPrice(cents) }

    fun clear() = listeners.clear()
}
