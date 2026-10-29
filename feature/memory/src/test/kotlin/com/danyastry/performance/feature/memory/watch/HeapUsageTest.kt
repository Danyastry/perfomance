package com.danyastry.performance.feature.memory.watch

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HeapUsageTest {

    @Test
    fun `converts bytes to whole megabytes`() {
        val usage = HeapUsage(usedBytes = 25L * 1024 * 1024 + 1, maxBytes = 512L * 1024 * 1024)

        assertThat(usage.usedMegabytes).isEqualTo(25)
        assertThat(usage.maxMegabytes).isEqualTo(512)
    }

    @Test
    fun `current usage is positive and below the limit`() {
        val usage = HeapUsage.current()

        assertThat(usage.usedBytes).isGreaterThan(0L)
        assertThat(usage.usedBytes).isAtMost(usage.maxBytes)
    }

    @Test
    fun `without LeakCanary the watcher reports nothing`() {
        assertThat(NoLeakWatcher.isAvailable).isFalse()
        assertThat(NoLeakWatcher.retainedObjectCount()).isEqualTo(0)
    }
}
