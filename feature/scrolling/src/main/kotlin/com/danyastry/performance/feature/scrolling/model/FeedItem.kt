package com.danyastry.performance.feature.scrolling.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface FeedItem {
    val id: Int

    @Immutable
    data class Header(override val id: Int, val title: String) : FeedItem

    @Immutable
    data class Product(override val id: Int, val name: String, val priceCents: Long, val rating: Int) : FeedItem
}
