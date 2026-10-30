package com.danyastry.performance.feature.runtime.data

import com.danyastry.performance.feature.runtime.model.GcCounters
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RuntimeStatsTest {

    @Test
    fun `reads ART counters by name`() {
        val stats = mapOf(
            "art.gc.gc-count" to "12",
            "art.gc.gc-time" to "340",
            "art.gc.blocking-gc-count" to "1",
            "art.gc.blocking-gc-time" to "25",
            "art.gc.bytes-allocated" to "1048576",
        )

        assertThat(RuntimeStats.read(stats::get)).isEqualTo(GcCounters(12, 340, 1, 25, 1_048_576))
    }

    @Test
    fun `missing or unreadable counters count as zero`() {
        val stats = mapOf("art.gc.gc-count" to "n/a", "art.gc.gc-time" to " 7 ")

        assertThat(RuntimeStats.read(stats::get)).isEqualTo(GcCounters(0, 7, 0, 0, 0))
    }

    @Test
    fun `subtracting gives the counts for one run`() {
        val before = GcCounters(10, 100, 1, 5, 1_000)
        val after = GcCounters(14, 160, 1, 5, 9_000)

        assertThat(after - before).isEqualTo(GcCounters(4, 60, 0, 0, 8_000))
    }
}
