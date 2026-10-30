package com.danyastry.performance.feature.mainthread.logic.parse

object ScanningReceiptParser : ReceiptParser {

    override fun parse(text: String): ParsedReceipt {
        val headerEnd = text.indexOf('\n')
        if (headerEnd < 0) return ParsedReceipt(0, 0, 0)
        val skus = SkuSet(text)
        var lines = 0
        var total = 0L
        var lineNumber = 1
        var start = headerEnd + 1
        while (start < text.length) {
            val newline = text.indexOf('\n', start).let { if (it < 0) text.length else it }
            val end = if (newline > start && text[newline - 1] == '\r') newline - 1 else newline
            lineNumber++
            if (!isBlank(text, start, end)) {
                val first = commaIn(text, start, end)
                val second = if (first < 0) -1 else commaIn(text, first + 1, end)
                val third = if (second < 0) -1 else commaIn(text, second + 1, end)
                if (third < 0 || commaIn(text, third + 1, end) >= 0) {
                    ReceiptFormat.wrongFieldCount(lineNumber, countCommas(text, start, end) + 1)
                }
                skus.add(start, first)
                val quantity = parseLong(text, second + 1, third)
                if (quantity < Int.MIN_VALUE || quantity > Int.MAX_VALUE) throw numberError(text, second + 1, third)
                total += quantity.toInt() * parseLong(text, third + 1, end)
                lines++
            }
            start = newline + 1
        }
        return ParsedReceipt(lines = lines, totalCents = total, distinctSkus = skus.size)
    }

    private fun commaIn(text: String, from: Int, end: Int): Int {
        for (index in from until end) if (text[index] == ',') return index
        return -1
    }

    private fun countCommas(text: String, start: Int, end: Int): Int {
        var count = 0
        for (index in start until end) if (text[index] == ',') count++
        return count
    }

    private fun isBlank(text: String, start: Int, end: Int): Boolean {
        for (index in start until end) if (!text[index].isWhitespace()) return false
        return true
    }

    private fun parseLong(text: String, start: Int, end: Int): Long {
        var index = start
        val negative = index < end && text[index] == '-'
        if (index < end && (text[index] == '-' || text[index] == '+')) index++
        if (index == end) throw numberError(text, start, end)
        var value = 0L
        while (index < end) {
            val digit = text[index] - '0'
            if (digit !in 0..9) throw numberError(text, start, end)
            if (value > (Long.MAX_VALUE - digit) / 10) throw numberError(text, start, end)
            value = value * 10 + digit
            index++
        }
        return if (negative) -value else value
    }

    private fun numberError(text: String, start: Int, end: Int) =
        NumberFormatException("For input string: \"${text.substring(start, end)}\"")
}
