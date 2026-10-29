package com.danyastry.performance.feature.memory.logic

import com.google.common.collect.Range
import com.google.common.truth.Truth.assertThat
import kotlin.random.Random
import org.junit.Test

class PriceTickerTest {

    @Test
    fun `prices are between 100 and 999 units`() {
        val random = Random(seed = 7)

        repeat(1_000) {
            assertThat(PriceTicker.next(random)).isIn(Range.closed(10_000L, 99_999L))
        }
    }
}
