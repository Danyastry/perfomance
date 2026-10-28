package com.danyastry.performance.feature.startup.logic

import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample

object StartupClock {

    fun sample(
        mode: InitMode,
        processStartUptime: Long,
        applicationCreatedUptime: Long,
        firstFrameUptime: Long,
        now: Long,
    ): StartupSample? {
        if (processStartUptime <= 0 || applicationCreatedUptime < processStartUptime || firstFrameUptime < applicationCreatedUptime) {
            return null
        }
        return StartupSample(
            mode = mode,
            applicationMillis = applicationCreatedUptime - processStartUptime,
            firstFrameMillis = firstFrameUptime - processStartUptime,
            timestampMillis = now,
        )
    }
}
