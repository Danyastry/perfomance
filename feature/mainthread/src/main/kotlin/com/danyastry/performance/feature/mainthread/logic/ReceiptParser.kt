package com.danyastry.performance.feature.mainthread.logic

object ReceiptParser {

    fun parse(text: String): ParsedReceipt {
        var lines = 0
        var total = 0L
        val skus = HashSet<String>()
        text.lineSequence().drop(1).filter { it.isNotBlank() }.forEach { line ->
            val fields = line.split(',')
            require(fields.size == FIELDS) { "Line ${lines + 2} has ${fields.size} fields, expected $FIELDS" }
            skus += fields[0]
            total += fields[2].toInt() * fields[3].toLong()
            lines++
        }
        return ParsedReceipt(lines = lines, totalCents = total, distinctSkus = skus.size)
    }

    private const val FIELDS = 4
}
