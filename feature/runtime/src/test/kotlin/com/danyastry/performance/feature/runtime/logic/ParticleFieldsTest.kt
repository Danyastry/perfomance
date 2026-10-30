package com.danyastry.performance.feature.runtime.logic

import com.google.common.collect.Range
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ParticleFieldsTest {

    @Test
    fun `both modes compute exactly the same positions`() {
        val allocating = AllocatingParticleField(count = 500, seed = 7)
        val packed = PackedParticleField(count = 500, seed = 7)

        repeat(600) { step ->
            val seconds = if (step % 10 == 0) 0.05f else 0.016f
            allocating.step(seconds)
            packed.step(seconds)
        }

        for (index in 0 until 500) {
            assertThat(packed.x(index)).isEqualTo(allocating.x(index))
            assertThat(packed.y(index)).isEqualTo(allocating.y(index))
        }
    }

    @Test
    fun `particles stay inside the unit square`() {
        val field = PackedParticleField(count = 1_000, seed = 3)

        repeat(1_000) { field.step(0.033f) }

        for (index in 0 until field.count) {
            assertThat(field.x(index)).isIn(Range.closed(0f, 1f))
            assertThat(field.y(index)).isIn(Range.closed(0f, 1f))
        }
    }

    @Test
    fun `drawn points follow the particles`() {
        val allocating = AllocatingParticleField(count = 10, seed = 1)
        val packed = PackedParticleField(count = 10, seed = 1)

        allocating.step(0.016f)
        packed.step(0.016f)

        assertThat(allocating.points.map { it.x }).isEqualTo((0 until 10).map { allocating.x(it) })
        assertThat(packed.points.toList().chunked(2).map { it[0] }).isEqualTo((0 until 10).map { packed.x(it) })
    }

    @Test
    fun `bounces reverse the velocity and reflect the position`() {
        assertThat(ParticleMotion.position(0.95f, 0.5f, 0.2f)).isWithin(1e-6f).of(0.95f)
        assertThat(ParticleMotion.velocity(0.95f, 0.5f, 0.2f)).isEqualTo(-0.5f)
        assertThat(ParticleMotion.position(0.05f, -0.5f, 0.2f)).isWithin(1e-6f).of(0.05f)
        assertThat(ParticleMotion.velocity(0.5f, 0.5f, 0.2f)).isEqualTo(0.5f)
    }
}
