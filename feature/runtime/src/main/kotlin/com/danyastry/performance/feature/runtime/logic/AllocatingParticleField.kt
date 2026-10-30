package com.danyastry.performance.feature.runtime.logic

import androidx.compose.ui.geometry.Offset
import com.danyastry.performance.feature.runtime.model.Particle

class AllocatingParticleField(override val count: Int, seed: Int) : ParticleField {

    var particles: List<Particle> = buildList(count) {
        ParticleMotion.seed(count, seed) { _, x, y, velocityX, velocityY -> add(Particle(x, y, velocityX, velocityY)) }
    }
        private set

    var points: List<Offset> = emptyList()
        private set

    override fun step(seconds: Float) {
        particles = particles.map { particle ->
            Particle(
                x = ParticleMotion.position(particle.x, particle.velocityX, seconds),
                y = ParticleMotion.position(particle.y, particle.velocityY, seconds),
                velocityX = ParticleMotion.velocity(particle.x, particle.velocityX, seconds),
                velocityY = ParticleMotion.velocity(particle.y, particle.velocityY, seconds),
            )
        }
        points = particles.map { Offset(it.x, it.y) }
    }

    override fun x(index: Int) = particles[index].x

    override fun y(index: Int) = particles[index].y
}
