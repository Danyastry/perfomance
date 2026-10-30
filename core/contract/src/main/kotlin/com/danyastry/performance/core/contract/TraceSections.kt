package com.danyastry.performance.core.contract

object TraceSections {
    const val RECEIPT_LOAD = "receipt.load"
    const val RECEIPT_READ = "receipt.read"
    const val RECEIPT_PARSE = "receipt.parse"
    const val RECEIPT_CHECKSUM = "receipt.checksum"

    val RECEIPT: List<String> = listOf(RECEIPT_LOAD, RECEIPT_READ, RECEIPT_PARSE, RECEIPT_CHECKSUM)
}
