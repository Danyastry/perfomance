package com.danyastry.performance.feature.runtime.logic

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WarmupScaleTest {

    @Test
    fun `warm batches are flat and slowdowns rise on a log scale`() {
        val heights = WarmupScale.heights(listOf(10L, 100L, 1_000L, 20L), warmMicros = 10)

        assertThat(heights[0]).isEqualTo(0f)
        assertThat(heights[1]).isWithin(1e-6f).of(0.5f)
        assertThat(heights[2]).isWithin(1e-6f).of(1f)
        assertThat(heights[3]).isWithin(1e-3f).of(0.151f)
    }

    @Test
    fun `faster than warm stays flat and huge slowdowns are capped`() {
        assertThat(WarmupScale.heights(listOf(5L, 1_000_000L), warmMicros = 10)).containsExactly(0f, 1f).inOrder()
    }

    @Test
    fun `a zero warm time does not divide by zero`() {
        assertThat(WarmupScale.heights(listOf(0L, 50L), warmMicros = 0)).hasSize(2)
    }
}
