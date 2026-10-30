package com.danyastry.performance.feature.startup.data

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
