package com.danyastry.performance.feature.scrolling.data

import com.danyastry.performance.feature.scrolling.model.FeedItem

object FeedSource {

    const val SIZE = 1_000
    private const val SECTION = 25

    fun items(): List<FeedItem> = (0 until SIZE).map { index ->
        if (index % SECTION == 0) {
            FeedItem.Header(index, "Section ${index / SECTION + 1}")
        } else {
            FeedItem.Product(index, "Product #$index", priceCents = 499L + (index * 37L) % 9_000, rating = index % 5 + 1)
        }
    }
}
