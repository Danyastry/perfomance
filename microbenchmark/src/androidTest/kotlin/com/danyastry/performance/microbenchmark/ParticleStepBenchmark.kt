package com.danyastry.performance.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danyastry.performance.feature.runtime.logic.AllocatingParticleField
import com.danyastry.performance.feature.runtime.logic.PackedParticleField
import com.danyastry.performance.feature.runtime.logic.ParticleField
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParticleStepBenchmark {

    @get:Rule
    val rule = BenchmarkRule()

    @Test
    fun allocateEveryFrame() = measure(AllocatingParticleField(PARTICLES, SEED))

    @Test
    fun reuseArrays() = measure(PackedParticleField(PARTICLES, SEED))

    private fun measure(field: ParticleField) {
        rule.measureRepeated { field.step(FRAME_SECONDS) }
    }

    private companion object {
        const val PARTICLES = 5_000
        const val SEED = 42
        const val FRAME_SECONDS = 0.016f
    }
}
