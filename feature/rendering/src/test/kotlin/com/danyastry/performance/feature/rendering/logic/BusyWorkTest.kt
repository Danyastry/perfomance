package com.danyastry.performance.feature.rendering.logic

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BusyWorkTest {

    @Test
    fun `spins until the requested time has passed`() {
        var now = 0L
        val spins = BusyWork.spin(millis = 2, nanoTime = { now.also { now += 250_000 } })

        assertThat(now).isAtLeast(2_000_000)
        assertThat(spins).isEqualTo(7)
    }

    @Test
    fun `really takes at least that long`() {
        val start = System.nanoTime()

        BusyWork.spin(millis = 5)

        assertThat(System.nanoTime() - start).isAtLeast(5_000_000)
    }
}
