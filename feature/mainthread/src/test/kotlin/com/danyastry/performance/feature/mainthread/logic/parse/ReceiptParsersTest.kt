package com.danyastry.performance.feature.mainthread.logic.parse

import com.danyastry.performance.feature.mainthread.logic.ReceiptGenerator
import com.danyastry.performance.feature.mainthread.model.ParserKind
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ReceiptParsersTest(private val kind: ParserKind) {

    private val parser = ReceiptParsers.of(kind)

    @Test
    fun `parses every generated line and adds up quantity times price`() {
        val lines = 12_345
        val expectedTotal = (0 until lines).sumOf { index ->
            ReceiptGenerator.quantity(index) * ReceiptGenerator.priceCents(index % ReceiptGenerator.DISTINCT_SKUS)
        }

        val parsed = parser.parse(ReceiptGenerator.text(lines))

        assertThat(parsed).isEqualTo(ParsedReceipt(lines = lines, totalCents = expectedTotal, distinctSkus = ReceiptGenerator.DISTINCT_SKUS))
    }

    @Test
    fun `counts distinct products when there are fewer lines than products`() {
        assertThat(parser.parse(ReceiptGenerator.text(10)).distinctSkus).isEqualTo(10)
    }

    @Test
    fun `header only and empty receipts are empty`() {
        assertThat(parser.parse(ReceiptGenerator.HEADER + "\n")).isEqualTo(ParsedReceipt(0, 0, 0))
        assertThat(parser.parse(ReceiptGenerator.HEADER)).isEqualTo(ParsedReceipt(0, 0, 0))
        assertThat(parser.parse("")).isEqualTo(ParsedReceipt(0, 0, 0))
    }

    @Test
    fun `handles CRLF, blank lines, signs, empty names and a missing final newline`() {
        val text = "h\r\nA,Item,2,100\r\n\r\n   \nB,,+3,-5\nA,x,1,1\n\nC,c,0,999999999999"

        assertThat(parser.parse(text)).isEqualTo(ParsedReceipt(lines = 4, totalCents = 186, distinctSkus = 3))
    }

    @Test
    fun `an empty product code still counts as one product`() {
        assertThat(parser.parse("h\n,a,1,1\n,b,1,1\n").distinctSkus).isEqualTo(1)
    }

    @Test
    fun `rejects too few or too many fields and names the real line`() {
        val few = assertThrows(IllegalArgumentException::class.java) { parser.parse("h\nA,a,1,1\n\nB,b\n") }
        val many = assertThrows(IllegalArgumentException::class.java) { parser.parse("h\nA,a,1,1,9\n") }

        assertThat(few).hasMessageThat().isEqualTo("Line 4 has 2 fields, expected 4")
        assertThat(many).hasMessageThat().isEqualTo("Line 2 has 5 fields, expected 4")
    }

    @Test
    fun `rejects numbers that are not numbers`() {
        listOf("h\nA,a,x,1\n", "h\nA,a,1,\n", "h\nA,a, 1,1\n", "h\nA,a,-,1\n", "h\nA,a,3000000000,1\n").forEach { text ->
            assertThrows(text, NumberFormatException::class.java) { parser.parse(text) }
        }
    }

    @Test
    fun `agrees with String split on many products`() {
        val text = buildString {
            appendLine("h")
            for (index in 0 until 20_000) appendLine("P${index * 7919 % 3_001},n,${index % 5},${index % 977}")
        }

        assertThat(parser.parse(text)).isEqualTo(SplitReceiptParser.parse(text))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun kinds() = ParserKind.entries.toList()
    }
}
