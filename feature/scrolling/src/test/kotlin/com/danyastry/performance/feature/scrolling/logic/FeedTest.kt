package com.danyastry.performance.feature.scrolling.logic

import com.danyastry.performance.feature.scrolling.data.FeedSource
import com.danyastry.performance.feature.scrolling.model.FeedItem
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FeedTest {

    @Test
    fun `the feed has a header every 25 rows and unique ids`() {
        val items = FeedSource.items()

        assertThat(items).hasSize(FeedSource.SIZE)
        assertThat(items.map { it.id }).containsNoDuplicates()
        assertThat(items.filterIsInstance<FeedItem.Header>()).hasSize(40)
        assertThat(items.first()).isInstanceOf(FeedItem.Header::class.java)
    }

    @Test
    fun `picks the largest power of two that keeps the thumbnail sharp`() {
        assertThat(ImageDecoding.sampleSize(2_400, 1_600, 160)).isEqualTo(8)
        assertThat(ImageDecoding.sampleSize(320, 320, 160)).isEqualTo(2)
        assertThat(ImageDecoding.sampleSize(100, 100, 160)).isEqualTo(1)
    }

    @Test
    fun `row text is the same every time it is built`() {
        val product = FeedItem.Product(7, "Product #7", priceCents = 1_234, rating = 3)

        val model = RowWork.model(product)

        assertThat(model.details).isEqualTo("$12.34, rated 3 of 5, item 00007")
        assertThat(RowWork.model(product)).isEqualTo(model)
    }
}
