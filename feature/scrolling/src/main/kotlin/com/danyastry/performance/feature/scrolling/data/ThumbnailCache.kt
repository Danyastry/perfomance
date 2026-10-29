package com.danyastry.performance.feature.scrolling.data

import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import com.danyastry.performance.feature.scrolling.logic.ImageDecoding

object ThumbnailCache {

    private const val ENTRIES = 120

    private val cache = LruCache<Int, ImageBitmap>(ENTRIES)

    fun cached(id: Int): ImageBitmap? = cache.get(id)

    fun load(id: Int, jpeg: ByteArray): ImageBitmap =
        cache.get(id) ?: ImageDecoding.subsampled(jpeg, SampleImage.WIDTH, SampleImage.HEIGHT).also { cache.put(id, it) }

    fun clear() = cache.evictAll()
}
