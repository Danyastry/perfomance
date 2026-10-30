package com.danyastry.performance.feature.runtime.data

import android.os.Debug
import com.danyastry.performance.feature.runtime.model.GcCounters

object RuntimeStats {

    fun read(stat: (String) -> String? = Debug::getRuntimeStat): GcCounters = GcCounters(
        gcCount = stat.long("art.gc.gc-count"),
        gcTimeMillis = stat.long("art.gc.gc-time"),
        blockingGcCount = stat.long("art.gc.blocking-gc-count"),
        blockingGcTimeMillis = stat.long("art.gc.blocking-gc-time"),
        bytesAllocated = stat.long("art.gc.bytes-allocated"),
    )

    private fun ((String) -> String?).long(key: String): Long = invoke(key)?.trim()?.toLongOrNull() ?: 0L
}
