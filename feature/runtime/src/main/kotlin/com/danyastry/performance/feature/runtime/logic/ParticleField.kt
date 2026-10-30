package com.danyastry.performance.feature.runtime.logic

interface ParticleField {
    val count: Int

    fun step(seconds: Float)

    fun x(index: Int): Float

    fun y(index: Int): Float
}
