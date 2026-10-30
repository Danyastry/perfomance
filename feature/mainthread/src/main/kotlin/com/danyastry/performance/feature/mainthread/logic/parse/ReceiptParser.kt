package com.danyastry.performance.feature.mainthread.logic.parse

interface ReceiptParser {
    fun parse(text: String): ParsedReceipt
}
