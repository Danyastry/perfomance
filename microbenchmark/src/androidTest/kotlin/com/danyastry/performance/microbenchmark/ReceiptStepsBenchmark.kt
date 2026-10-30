package com.danyastry.performance.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danyastry.performance.feature.mainthread.logic.ReceiptChecksum
import com.danyastry.performance.feature.mainthread.logic.ReceiptGenerator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReceiptStepsBenchmark {

    @get:Rule
    val rule = BenchmarkRule()

    @Test
    fun decodeUtf8() {
        rule.measureRepeated { BYTES.decodeToString() }
    }

    @Test
    fun checksumOneRound() {
        rule.measureRepeated { ReceiptChecksum.of(BYTES, rounds = 1) }
    }

    private companion object {
        val BYTES = ReceiptGenerator.text(10_000).encodeToByteArray()
    }
}
