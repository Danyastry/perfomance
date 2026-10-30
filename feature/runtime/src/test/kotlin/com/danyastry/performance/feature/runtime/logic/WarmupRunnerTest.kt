package com.danyastry.performance.feature.runtime.logic

import com.google.common.truth.Truth.assertThat
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Test

class WarmupRunnerTest {

    @Test
    fun `times every batch and keeps the checksum`() {
        var now = 0L
        val runner = WarmupRunner(nanoTime = { now.also { now += 1_500 } }, firstRun = AtomicBoolean(true))
        val input = Crc32Workload.input(64)

        val run = runner.run(batches = 4, input = input)

        assertThat(run.batchMicros).containsExactly(1L, 1L, 1L, 1L)
        assertThat(run.checksum).isEqualTo(0L)
    }

    @Test
    fun `an odd number of batches leaves the input checksum`() {
        val input = Crc32Workload.input(64)

        val run = WarmupRunner(firstRun = AtomicBoolean(true)).run(batches = 3, input = input)

        assertThat(run.checksum).isEqualTo(Crc32Workload.crc32(input))
        assertThat(run.batchMicros).hasSize(3)
    }

    @Test
    fun `only the first run in the process is marked as first`() {
        val flag = AtomicBoolean(true)

        val first = WarmupRunner(firstRun = flag).run(batches = 1)
        val second = WarmupRunner(firstRun = flag).run(batches = 1)

        assertThat(first.firstInProcess).isTrue()
        assertThat(second.firstInProcess).isFalse()
    }
}
