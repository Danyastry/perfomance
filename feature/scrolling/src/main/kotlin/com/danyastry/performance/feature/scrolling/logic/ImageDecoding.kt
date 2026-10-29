package com.danyastry.performance.feature.scrolling.logic

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.scale

object ImageDecoding {

    const val THUMBNAIL_PX = 160

    fun fullSizeThenScale(jpeg: ByteArray): ImageBitmap {
        val full = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size)
        val thumbnail = full.scale(THUMBNAIL_PX, THUMBNAIL_PX)
        full.recycle()
        return thumbnail.asImageBitmap()
    }

    fun subsampled(jpeg: ByteArray, sourceWidth: Int, sourceHeight: Int): ImageBitmap {
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize(sourceWidth, sourceHeight, THUMBNAIL_PX) }
        val small = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size, options)
        val thumbnail = small.scale(THUMBNAIL_PX, THUMBNAIL_PX)
        if (thumbnail !== small) small.recycle()
        return thumbnail.asImageBitmap()
    }

    fun sampleSize(width: Int, height: Int, target: Int): Int {
        var sample = 1
        while (width / (sample * 2) >= target && height / (sample * 2) >= target) sample *= 2
        return sample
    }
}
