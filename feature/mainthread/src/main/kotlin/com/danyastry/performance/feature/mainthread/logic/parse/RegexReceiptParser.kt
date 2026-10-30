package com.danyastry.performance.feature.mainthread.logic.parse

object RegexReceiptParser : ReceiptParser {

    private val LINE = Regex("([^,]*),([^,]*),([^,]*),([^,]*)")

    override fun parse(text: String): ParsedReceipt {
        var lines = 0
        var total = 0L
        val skus = HashSet<String>()
        text.lineSequence().forEachIndexed { index, line ->
            if (index == 0 || line.isBlank()) return@forEachIndexed
            val match = LINE.matchEntire(line) ?: ReceiptFormat.wrongFieldCount(index + 1, line.count { it == ',' } + 1)
            val (sku, _, quantity, price) = match.destructured
            skus += sku
            total += quantity.toInt() * price.toLong()
            lines++
        }
        return ParsedReceipt(lines = lines, totalCents = total, distinctSkus = skus.size)
    }
}
