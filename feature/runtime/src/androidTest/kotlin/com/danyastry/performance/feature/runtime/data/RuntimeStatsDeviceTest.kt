package com.danyastry.performance.feature.runtime.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RuntimeStatsDeviceTest {

    @Test
    fun artReportsAllocationsAndCollections() {
        val before = RuntimeStats.read()

        val kept = ArrayList<ByteArray>(ALLOCATIONS)
        repeat(ALLOCATIONS) { kept += ByteArray(CHUNK_BYTES) }
        val allocated = RuntimeStats.read()
        kept.clear()
        Runtime.getRuntime().gc()
        val after = RuntimeStats.read()

        assertThat(allocated.bytesAllocated - before.bytesAllocated).isAtLeast(ALLOCATIONS.toLong() * CHUNK_BYTES)
        assertThat(after.bytesAllocated - before.bytesAllocated).isAtLeast(ALLOCATIONS.toLong() * CHUNK_BYTES)
        assertThat(after.gcCount).isGreaterThan(before.gcCount)
        assertThat(after.gcTimeMillis).isAtLeast(before.gcTimeMillis)
    }

    private companion object {
        const val ALLOCATIONS = 2_000
        const val CHUNK_BYTES = 4_096
    }
}
