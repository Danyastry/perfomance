package com.danyastry.performance.feature.memory.watch

data class HeapUsage(val usedBytes: Long, val maxBytes: Long) {

    val usedMegabytes: Long
        get() = usedBytes / BYTES_PER_MB

    val maxMegabytes: Long
        get() = maxBytes / BYTES_PER_MB

    companion object {
        private const val BYTES_PER_MB = 1024L * 1024L

        fun current(runtime: Runtime = Runtime.getRuntime()): HeapUsage =
            HeapUsage(runtime.totalMemory() - runtime.freeMemory(), runtime.maxMemory())
    }
}
