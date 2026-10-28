package com.danyastry.performance.feature.startup.data

import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SampleCodecTest {

    @Test
    fun `round trips a sample`() {
        val sample = StartupSample(InitMode.BACKGROUND, 120, 480, 1_700_000_000_000)

        assertThat(SampleCodec.decode(SampleCodec.encode(sample))).isEqualTo(sample)
    }

    @Test
    fun `skips lines it cannot read`() {
        listOf("", "EAGER,1,2", "FAST,1,2,3", "EAGER,x,2,3").forEach { assertThat(SampleCodec.decode(it)).isNull() }
    }

    @Test
    fun `the heavy library initializes once and gives the same result`() {
        val first = HeavyLibrary.initialize()

        assertThat(HeavyLibrary.isReady).isTrue()
        assertThat(HeavyLibrary.initialize()).isEqualTo(first)
        assertThat(first).hasLength(16)
    }
}
