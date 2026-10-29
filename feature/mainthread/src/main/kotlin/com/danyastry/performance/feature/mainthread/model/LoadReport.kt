package com.danyastry.performance.feature.mainthread.model

data class LoadReport(
    val mode: LoadMode,
    val summary: ReceiptSummary,
    val workMillis: Long,
    val frames: FrameGaps,
)
