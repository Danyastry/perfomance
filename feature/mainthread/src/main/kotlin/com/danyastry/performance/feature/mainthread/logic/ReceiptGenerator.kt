package com.danyastry.performance.feature.mainthread.logic

object ReceiptGenerator {

    const val HEADER = "sku,name,quantity,price_cents"

    fun text(lines: Int): String = buildString {
        appendLine(HEADER)
        for (index in 0 until lines) {
            val sku = index % DISTINCT_SKUS
            append("SKU-").append(sku.toString().padStart(5, '0'))
            append(",Item ").append(sku)
            append(',').append(quantity(index))
            append(',').append(priceCents(sku))
            append('\n')
        }
    }

    fun quantity(index: Int): Int = index % MAX_QUANTITY + 1

    fun priceCents(sku: Int): Long = 99L + (sku * PRICE_STEP) % MAX_PRICE_CENTS

    const val DISTINCT_SKUS = 5_000
    private const val MAX_QUANTITY = 4
    private const val PRICE_STEP = 37
    private const val MAX_PRICE_CENTS = 20_000
}
