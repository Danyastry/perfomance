package com.danyastry.performance.feature.mainthread.model

data class ReceiptSummary(
    val lines: Int,
    val totalCents: Long,
    val distinctSkus: Int,
    val checksum: String,
)
