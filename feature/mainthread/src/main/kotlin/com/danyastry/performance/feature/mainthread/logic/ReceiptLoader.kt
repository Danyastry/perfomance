package com.danyastry.performance.feature.mainthread.logic

import android.os.StrictMode
import androidx.tracing.trace
import com.danyastry.performance.feature.mainthread.data.ReceiptFile
import com.danyastry.performance.feature.mainthread.logic.parse.ReceiptParser
import com.danyastry.performance.feature.mainthread.logic.parse.SplitReceiptParser
import com.danyastry.performance.feature.mainthread.model.ReceiptSummary

class ReceiptLoader(
    private val checksumRounds: Int = CHECKSUM_ROUNDS,
    private val noteSlowCall: (String) -> Unit = StrictMode::noteSlowCall,
) {

    fun load(receipt: ReceiptFile, parser: ReceiptParser = SplitReceiptParser): ReceiptSummary = trace(SECTION_LOAD) {
        val bytes = trace(SECTION_READ) { receipt.readBytes() }
        val parsed = trace(SECTION_PARSE) { parser.parse(bytes.decodeToString()) }
        val checksum = trace(SECTION_CHECKSUM) {
            noteSlowCall(SECTION_CHECKSUM)
            ReceiptChecksum.of(bytes, checksumRounds)
        }
        ReceiptSummary(parsed.lines, parsed.totalCents, parsed.distinctSkus, checksum)
    }

    companion object {
        const val SECTION_LOAD = "receipt.load"
        const val SECTION_READ = "receipt.read"
        const val SECTION_PARSE = "receipt.parse"
        const val SECTION_CHECKSUM = "receipt.checksum"
        const val CHECKSUM_ROUNDS = 8
    }
}
