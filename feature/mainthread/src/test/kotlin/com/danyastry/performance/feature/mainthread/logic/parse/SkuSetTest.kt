package com.danyastry.performance.feature.mainthread.logic.parse

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SkuSetTest {

    @Test
    fun `counts equal spans once wherever they are in the text`() {
        val text = "abc,abc,abd,ab"
        val set = SkuSet(text)

        set.add(0, 3)
        set.add(4, 7)
        set.add(8, 11)
        set.add(12, 14)

        assertThat(set.size).isEqualTo(3)
    }

    @Test
    fun `keeps every entry while growing far past its first capacity`() {
        val codes = (0 until 10_000).map { "SKU-$it" }
        val text = codes.joinToString(",")
        val set = SkuSet(text, expected = 1)
        var start = 0
        repeat(2) {
            start = 0
            codes.forEach { code ->
                set.add(start, start + code.length)
                start += code.length + 1
            }
        }

        assertThat(set.size).isEqualTo(10_000)
    }

    @Test
    fun `colliding hashes with different text stay separate`() {
        val text = "Aa,BB"
        val set = SkuSet(text)

        set.add(0, 2)
        set.add(3, 5)

        assertThat("Aa".hashCode()).isEqualTo("BB".hashCode())
        assertThat(set.size).isEqualTo(2)
    }
}
