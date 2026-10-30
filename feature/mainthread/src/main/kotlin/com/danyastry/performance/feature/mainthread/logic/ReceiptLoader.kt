package com.danyastry.performance.feature.mainthread.logic

import android.os.StrictMode
import androidx.tracing.trace
import com.danyastry.performance.core.contract.TraceSections
import com.danyastry.performance.feature.mainthread.data.ReceiptFile
import com.danyastry.performance.feature.mainthread.logic.parse.ReceiptParser
import com.danyastry.performance.feature.mainthread.logic.parse.SplitReceiptParser
import com.danyastry.performance.feature.mainthread.model.ReceiptSummary

class ReceiptLoader(
    private val checksumRounds: Int = CHECKSUM_ROUNDS,
    private val noteSlowCall: (String) -> Unit = StrictMode::noteSlowCall,
) {

    fun load(receipt: ReceiptFile, parser: ReceiptParser = SplitReceiptParser): ReceiptSummary = trace(TraceSections.RECEIPT_LOAD) {
        val bytes = trace(TraceSections.RECEIPT_READ) { receipt.readBytes() }
        val parsed = trace(TraceSections.RECEIPT_PARSE) { parser.parse(bytes.decodeToString()) }
        val checksum = trace(TraceSections.RECEIPT_CHECKSUM) {
            noteSlowCall(TraceSections.RECEIPT_CHECKSUM)
            ReceiptChecksum.of(bytes, checksumRounds)
        }
        ReceiptSummary(parsed.lines, parsed.totalCents, parsed.distinctSkus, checksum)
    }

    companion object {
        const val CHECKSUM_ROUNDS = 8
    }
}
