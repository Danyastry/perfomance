package com.danyastry.performance.feature.mainthread.logic

import com.danyastry.performance.feature.mainthread.data.ReceiptFile
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ReceiptLoaderTest {

    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun `loads, parses and hashes the receipt and marks the slow step`() {
        val slowCalls = mutableListOf<String>()
        val receipt = ReceiptFile(folder.root, lines = 1_000)
        val loader = ReceiptLoader(checksumRounds = 1, noteSlowCall = { slowCalls += it })

        val summary = loader.load(receipt)

        val bytes = receipt.file.readBytes()
        val parsed = ReceiptParser.parse(bytes.decodeToString())
        assertThat(summary.lines).isEqualTo(1_000)
        assertThat(summary.totalCents).isEqualTo(parsed.totalCents)
        assertThat(summary.distinctSkus).isEqualTo(1_000)
        assertThat(summary.checksum).isEqualTo(ReceiptChecksum.of(bytes, rounds = 1))
        assertThat(slowCalls).containsExactly(ReceiptLoader.SECTION_CHECKSUM)
    }
}
