package com.danyastry.performance.feature.startup.data

import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample

interface StartupStore {
    var mode: InitMode

    fun samples(): List<StartupSample>

    fun add(sample: StartupSample)

    fun clear()
}
