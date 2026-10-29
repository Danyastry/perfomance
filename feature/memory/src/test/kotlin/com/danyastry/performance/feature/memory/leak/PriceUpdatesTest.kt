package com.danyastry.performance.feature.memory.leak

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test

class PriceUpdatesTest {

    @After
    fun tearDown() = PriceUpdates.clear()

    @Test
    fun `delivers prices to every registered listener`() {
        val first = mutableListOf<Long>()
        val second = mutableListOf<Long>()
        PriceUpdates.register { first += it }
        PriceUpdates.register { second += it }

        PriceUpdates.publish(1_250)

        assertThat(first).containsExactly(1_250L)
        assertThat(second).containsExactly(1_250L)
        assertThat(PriceUpdates.listenerCount).isEqualTo(2)
    }

    @Test
    fun `unregistered listeners stop receiving prices and are no longer held`() {
        val received = mutableListOf<Long>()
        val listener = PriceListener { received += it }
        PriceUpdates.register(listener)

        PriceUpdates.unregister(listener)
        PriceUpdates.publish(900)

        assertThat(received).isEmpty()
        assertThat(PriceUpdates.listenerCount).isEqualTo(0)
    }

    @Test
    fun `a listener may unregister itself while prices are delivered`() {
        val received = mutableListOf<Long>()
        val once = object : PriceListener {
            override fun onPrice(cents: Long) {
                received += cents
                PriceUpdates.unregister(this)
            }
        }
        PriceUpdates.register(once)

        PriceUpdates.publish(100)
        PriceUpdates.publish(200)

        assertThat(received).containsExactly(100L)
    }

    @Test
    fun `clear drops every listener`() {
        repeat(3) { PriceUpdates.register { } }

        PriceUpdates.clear()

        assertThat(PriceUpdates.listenerCount).isEqualTo(0)
    }
}
