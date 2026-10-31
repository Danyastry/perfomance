package com.danyastry.performance.feature.runtime.logic

import com.danyastry.performance.feature.runtime.model.WarmupSummary
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class WarmupStatsTest {

    @Test
    fun `finds the warm level and the batch where the code got fast`() {
        val batches = listOf(900L, 850L, 700L, 300L, 40L) + List(60) { 20L }

        assertThat(WarmupStats.summarize(batches)).isEqualTo(
            WarmupSummary(firstMicros = 900, warmMicros = 20, speedup = 45.0, warmFromBatch = 5),
        )
    }

    @Test
    fun `already compiled code is warm from the first batch`() {
        val summary = WarmupStats.summarize(List(100) { 21L + it % 3 })

        assertThat(summary.warmFromBatch).isEqualTo(1)
        assertThat(summary.speedup).isWithin(0.1).of(1.0)
    }

    @Test
    fun `a zero warm time does not divide by zero`() {
        assertThat(WarmupStats.summarize(listOf(5L, 0L, 0L)).speedup).isEqualTo(5.0)
    }

    @Test
    fun `rejects an empty run`() {
        assertThrows(IllegalArgumentException::class.java) { WarmupStats.summarize(emptyList()) }
    }
}
