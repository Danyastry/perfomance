package com.danyastry.performance.feature.runtime.logic

import kotlin.random.Random

object ParticleMotion {

    fun seed(count: Int, seed: Int, write: (index: Int, x: Float, y: Float, velocityX: Float, velocityY: Float) -> Unit) {
        val random = Random(seed)
        repeat(count) { index ->
            write(index, random.nextFloat(), random.nextFloat(), velocity(random), velocity(random))
        }
    }

    fun position(position: Float, velocity: Float, seconds: Float): Float {
        val moved = position + velocity * seconds
        return when {
            moved < 0f -> -moved
            moved > 1f -> 2f - moved
            else -> moved
        }
    }

    fun velocity(position: Float, velocity: Float, seconds: Float): Float {
        val moved = position + velocity * seconds
        return if (moved < 0f || moved > 1f) -velocity else velocity
    }

    private fun velocity(random: Random): Float = (random.nextFloat() - 0.5f) * MAX_SPEED

    private const val MAX_SPEED = 0.6f
}
