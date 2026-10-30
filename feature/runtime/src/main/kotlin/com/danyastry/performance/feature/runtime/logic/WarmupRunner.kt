package com.danyastry.performance.feature.runtime.logic

import com.danyastry.performance.feature.runtime.model.WarmupRun
import java.util.concurrent.atomic.AtomicBoolean

class WarmupRunner(
    private val nanoTime: () -> Long = System::nanoTime,
    private val firstRun: AtomicBoolean = PROCESS_FIRST_RUN,
) {

    fun run(batches: Int = BATCHES, input: ByteArray = INPUT): WarmupRun {
        require(batches > 0) { "batches must be positive, was $batches" }
        val first = firstRun.getAndSet(false)
        val times = ArrayList<Long>(batches)
        var checksum = 0L
        repeat(batches) {
            val start = nanoTime()
            checksum = checksum xor Crc32Workload.crc32(input)
            times += (nanoTime() - start) / NANOS_PER_MICRO
        }
        return WarmupRun(batchMicros = times, checksum = checksum, firstInProcess = first)
    }

    companion object {
        const val BATCHES = 300
        const val INPUT_BYTES = 16 * 1024
        private const val NANOS_PER_MICRO = 1_000L
        private val INPUT = Crc32Workload.input(INPUT_BYTES)
        private val PROCESS_FIRST_RUN = AtomicBoolean(true)
    }
}
