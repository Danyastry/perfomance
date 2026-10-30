package com.danyastry.performance.feature.mainthread.logic.parse

import com.danyastry.performance.feature.mainthread.model.ParserKind

object ReceiptParsers {

    fun of(kind: ParserKind): ReceiptParser = when (kind) {
        ParserKind.REGEX -> RegexReceiptParser
        ParserKind.SPLIT -> SplitReceiptParser
        ParserKind.SCAN -> ScanningReceiptParser
    }
}
