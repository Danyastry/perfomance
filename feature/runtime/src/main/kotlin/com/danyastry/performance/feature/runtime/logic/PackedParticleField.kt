package com.danyastry.performance.feature.runtime.logic

class PackedParticleField(override val count: Int, seed: Int) : ParticleField {

    private val state = FloatArray(count * FIELDS)

    val points = FloatArray(count * 2)

    init {
        ParticleMotion.seed(count, seed) { index, x, y, velocityX, velocityY ->
            val base = index * FIELDS
            state[base] = x
            state[base + 1] = y
            state[base + 2] = velocityX
            state[base + 3] = velocityY
        }
    }

    override fun step(seconds: Float) {
        for (index in 0 until count) {
            val base = index * FIELDS
            val x = state[base]
            val y = state[base + 1]
            val velocityX = state[base + 2]
            val velocityY = state[base + 3]
            state[base] = ParticleMotion.position(x, velocityX, seconds)
            state[base + 1] = ParticleMotion.position(y, velocityY, seconds)
            state[base + 2] = ParticleMotion.velocity(x, velocityX, seconds)
            state[base + 3] = ParticleMotion.velocity(y, velocityY, seconds)
            points[index * 2] = state[base]
            points[index * 2 + 1] = state[base + 1]
        }
    }

    override fun x(index: Int) = state[index * FIELDS]

    override fun y(index: Int) = state[index * FIELDS + 1]

    private companion object {
        const val FIELDS = 4
    }
}
