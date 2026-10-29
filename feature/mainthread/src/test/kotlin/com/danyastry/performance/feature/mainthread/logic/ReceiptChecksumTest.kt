package com.danyastry.performance.feature.mainthread.logic

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class ReceiptChecksumTest {

    @Test
    fun `one round is plain SHA-256`() {
        assertThat(ReceiptChecksum.of("abc".encodeToByteArray(), rounds = 1))
            .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad")
    }

    @Test
    fun `more rounds give a different but stable digest`() {
        val bytes = "abc".encodeToByteArray()

        val twice = ReceiptChecksum.of(bytes, rounds = 2)

        assertThat(twice).isNotEqualTo(ReceiptChecksum.of(bytes, rounds = 1))
        assertThat(twice).isEqualTo(ReceiptChecksum.of(bytes, rounds = 2))
        assertThat(twice).hasLength(64)
    }

    @Test
    fun `rejects zero rounds`() {
        assertThrows(IllegalArgumentException::class.java) { ReceiptChecksum.of(ByteArray(1), rounds = 0) }
    }
}
