package com.danyastry.performance.core.ui.stats

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class MedianTest {

    @Test
    fun `odd counts take the middle value`() {
        assertThat(Median.of(listOf(3L, 1L, 2L))).isEqualTo(2)
    }

    @Test
    fun `even counts average the two middle values`() {
        assertThat(Median.of(listOf(4L, 1L, 3L, 2L))).isEqualTo(2)
        assertThat(Median.of(listOf(10L, 20L))).isEqualTo(15)
    }

    @Test
    fun `input order does not matter`() {
        assertThat(Median.of(listOf(900L, 700L, 800L))).isEqualTo(Median.of(listOf(700L, 800L, 900L)))
    }

    @Test
    fun `rejects an empty list`() {
        assertThrows(IllegalArgumentException::class.java) { Median.of(emptyList()) }
    }
}
