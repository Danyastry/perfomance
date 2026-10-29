package com.danyastry.performance.feature.mainthread.logic

data class ParsedReceipt(
    val lines: Int,
    val totalCents: Long,
    val distinctSkus: Int,
)
