package com.danyastry.performance.feature.mainthread.logic.parse

object SplitReceiptParser : ReceiptParser {

    override fun parse(text: String): ParsedReceipt {
        var lines = 0
        var total = 0L
        val skus = HashSet<String>()
        text.lineSequence().forEachIndexed { index, line ->
            if (index == 0 || line.isBlank()) return@forEachIndexed
            val fields = line.split(',')
            if (fields.size != ReceiptFormat.FIELDS) ReceiptFormat.wrongFieldCount(index + 1, fields.size)
            skus += fields[0]
            total += fields[2].toInt() * fields[3].toLong()
            lines++
        }
        return ParsedReceipt(lines = lines, totalCents = total, distinctSkus = skus.size)
    }
}
