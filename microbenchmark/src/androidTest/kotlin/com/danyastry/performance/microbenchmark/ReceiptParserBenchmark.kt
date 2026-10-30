package com.danyastry.performance.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danyastry.performance.feature.mainthread.logic.ReceiptGenerator
import com.danyastry.performance.feature.mainthread.logic.parse.ParsedReceipt
import com.danyastry.performance.feature.mainthread.logic.parse.ReceiptParser
import com.danyastry.performance.feature.mainthread.logic.parse.RegexReceiptParser
import com.danyastry.performance.feature.mainthread.logic.parse.ScanningReceiptParser
import com.danyastry.performance.feature.mainthread.logic.parse.SplitReceiptParser
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReceiptParserBenchmark {

    @get:Rule
    val rule = BenchmarkRule()

    @Test
    fun regex() = measure(RegexReceiptParser)

    @Test
    fun split() = measure(SplitReceiptParser)

    @Test
    fun indexScan() = measure(ScanningReceiptParser)

    private fun measure(parser: ReceiptParser) {
        var result: ParsedReceipt? = null
        rule.measureRepeated {
            result = parser.parse(TEXT)
        }
        assertThat(result).isEqualTo(EXPECTED)
    }

    private companion object {
        const val LINES = 10_000
        val TEXT = ReceiptGenerator.text(LINES)
        val EXPECTED = SplitReceiptParser.parse(TEXT)
    }
}
