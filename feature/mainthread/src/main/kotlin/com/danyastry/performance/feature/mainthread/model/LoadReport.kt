package com.danyastry.performance.feature.mainthread.model

import com.danyastry.performance.core.ui.frames.FrameGaps

data class LoadReport(
    val mode: LoadMode,
    val parser: ParserKind,
    val summary: ReceiptSummary,
    val workMillis: Long,
    val frames: FrameGaps,
)
