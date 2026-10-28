package com.danyastry.performance.feature.recomposition.tracking

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecompositionCountersTest {

    @Test
    fun `counts per id and resets`() {
        val counters = RecompositionCounters()

        repeat(3) { counters.increment("a") }
        counters.increment("b")

        assertThat(counters.counts.value).containsExactly("a", 3, "b", 1)
        counters.reset()
        assertThat(counters.count("a")).isEqualTo(0)
    }
}
