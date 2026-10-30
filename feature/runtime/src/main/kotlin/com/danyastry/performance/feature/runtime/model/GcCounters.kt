package com.danyastry.performance.feature.runtime.model

data class GcCounters(
    val gcCount: Long,
    val gcTimeMillis: Long,
    val blockingGcCount: Long,
    val blockingGcTimeMillis: Long,
    val bytesAllocated: Long,
) {
    operator fun minus(earlier: GcCounters) = GcCounters(
        gcCount = gcCount - earlier.gcCount,
        gcTimeMillis = gcTimeMillis - earlier.gcTimeMillis,
        blockingGcCount = blockingGcCount - earlier.blockingGcCount,
        blockingGcTimeMillis = blockingGcTimeMillis - earlier.blockingGcTimeMillis,
        bytesAllocated = bytesAllocated - earlier.bytesAllocated,
    )

    companion object {
        val ZERO = GcCounters(0, 0, 0, 0, 0)
    }
}
