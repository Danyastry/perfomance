package com.danyastry.performance.feature.mainthread.logic

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class ReceiptParserTest {

    @Test
    fun `parses every generated line and adds up quantity times price`() {
        val lines = 12_345
        val expectedTotal = (0 until lines).sumOf { index ->
            ReceiptGenerator.quantity(index) * ReceiptGenerator.priceCents(index % ReceiptGenerator.DISTINCT_SKUS)
        }

        val parsed = ReceiptParser.parse(ReceiptGenerator.text(lines))

        assertThat(parsed).isEqualTo(ParsedReceipt(lines = lines, totalCents = expectedTotal, distinctSkus = ReceiptGenerator.DISTINCT_SKUS))
    }

    @Test
    fun `counts distinct products when there are fewer lines than products`() {
        assertThat(ReceiptParser.parse(ReceiptGenerator.text(10)).distinctSkus).isEqualTo(10)
    }

    @Test
    fun `header only receipt is empty`() {
        assertThat(ReceiptParser.parse(ReceiptGenerator.HEADER + "\n")).isEqualTo(ParsedReceipt(0, 0, 0))
    }

    @Test
    fun `rejects a line with missing fields and names the line`() {
        val text = ReceiptGenerator.HEADER + "\nSKU-1,Item 1,2,100\nSKU-2,Item 2\n"

        val error = assertThrows(IllegalArgumentException::class.java) { ReceiptParser.parse(text) }

        assertThat(error).hasMessageThat().contains("Line 3")
    }
}
