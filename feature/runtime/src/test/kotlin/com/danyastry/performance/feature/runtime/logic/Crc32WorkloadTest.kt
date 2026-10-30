package com.danyastry.performance.feature.runtime.logic

import com.google.common.truth.Truth.assertThat
import java.util.zip.CRC32
import org.junit.Test

class Crc32WorkloadTest {

    @Test
    fun `matches the standard check value`() {
        assertThat(Crc32Workload.crc32("123456789".encodeToByteArray())).isEqualTo(0xCBF43926L)
    }

    @Test
    fun `matches java util zip CRC32 on the workload input`() {
        val input = Crc32Workload.input(WarmupRunner.INPUT_BYTES)
        val reference = CRC32().apply { update(input) }.value

        assertThat(Crc32Workload.crc32(input)).isEqualTo(reference)
    }

    @Test
    fun `empty input has checksum zero`() {
        assertThat(Crc32Workload.crc32(ByteArray(0))).isEqualTo(0L)
    }
}
