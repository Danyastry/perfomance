package com.danyastry.performance.feature.mainthread.logic.parse

object ReceiptFormat {
    const val FIELDS = 4

    fun wrongFieldCount(lineNumber: Int, fields: Int): Nothing =
        throw IllegalArgumentException("Line $lineNumber has $fields fields, expected $FIELDS")
}
