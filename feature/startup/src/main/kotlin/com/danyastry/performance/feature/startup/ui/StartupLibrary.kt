package com.danyastry.performance.feature.startup.ui

import com.danyastry.performance.feature.startup.data.HeavyLibrary

interface StartupLibrary {
    fun isReady(): Boolean

    fun initialize()

    companion object {
        val heavy: StartupLibrary = object : StartupLibrary {
            override fun isReady() = HeavyLibrary.isReady

            override fun initialize() {
                HeavyLibrary.initialize()
            }
        }
    }
}
