package com.danyastry.performance.feature.mainthread.data

import com.danyastry.performance.feature.mainthread.logic.ReceiptGenerator
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ReceiptFileTest {

    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun `writes the receipt once and then reuses it`() {
        val receipt = ReceiptFile(folder.root, lines = 50)

        val first = receipt.ensureWritten()
        first.setLastModified(1_000L)
        val second = receipt.ensureWritten()

        assertThat(second).isEqualTo(first)
        assertThat(second.lastModified()).isEqualTo(1_000L)
        assertThat(second.readText()).isEqualTo(ReceiptGenerator.text(50))
        assertThat(folder.root.list()).asList().containsExactly("receipt-50.csv")
    }

    @Test
    fun `reading creates the file when it is missing`() {
        val bytes = ReceiptFile(folder.root, lines = 3).readBytes()

        assertThat(bytes.decodeToString()).isEqualTo(ReceiptGenerator.text(3))
    }
}
