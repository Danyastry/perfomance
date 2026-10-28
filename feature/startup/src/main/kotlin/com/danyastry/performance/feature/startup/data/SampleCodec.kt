package com.danyastry.performance.feature.startup.data

import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample

object SampleCodec {

    fun encode(sample: StartupSample): String =
        listOf(sample.mode.name, sample.applicationMillis, sample.firstFrameMillis, sample.timestampMillis).joinToString(",")

    fun decode(line: String): StartupSample? {
        val parts = line.split(",")
        if (parts.size != 4) return null
        val mode = InitMode.entries.firstOrNull { it.name == parts[0] } ?: return null
        val numbers = parts.drop(1).map { it.toLongOrNull() ?: return null }
        return StartupSample(mode, numbers[0], numbers[1], numbers[2])
    }
}
