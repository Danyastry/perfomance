package com.danyastry.performance.feature.scrolling.logic

import com.danyastry.performance.feature.scrolling.model.FeedItem
import com.danyastry.performance.feature.scrolling.model.ProductRowModel
import java.text.NumberFormat
import java.util.Locale

object RowWork {

    private const val FORMAT_ROUNDS = 40

    fun model(product: FeedItem.Product): ProductRowModel {
        var details = ""
        repeat(FORMAT_ROUNDS) {
            val price = NumberFormat.getCurrencyInstance(Locale.US).format(product.priceCents / 100.0)
            details = String.format(Locale.US, "%s, rated %d of 5, item %05d", price, product.rating, product.id)
        }
        return ProductRowModel(product.id, product.name, details)
    }
}
