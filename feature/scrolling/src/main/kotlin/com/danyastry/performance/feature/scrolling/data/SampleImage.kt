package com.danyastry.performance.feature.scrolling.data

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import kotlin.random.Random

object SampleImage {

    const val WIDTH = 2_400
    const val HEIGHT = 1_600
    private const val QUALITY = 90

    @Volatile
    private var jpeg: ByteArray? = null

    fun jpegOrNull(): ByteArray? = jpeg

    @Synchronized
    fun jpeg(): ByteArray = jpeg ?: encode().also { jpeg = it }

    private fun encode(): ByteArray {
        val random = Random(SEED)
        val pixels = IntArray(WIDTH * HEIGHT) { index ->
            val x = index % WIDTH
            val y = index / WIDTH
            val noise = random.nextInt(48)
            val r = (x * 255 / WIDTH + noise) and 0xFF
            val g = (y * 255 / HEIGHT + noise) and 0xFF
            val b = ((x xor y) + noise) and 0xFF
            (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }
        val bitmap = Bitmap.createBitmap(pixels, WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        return ByteArrayOutputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, out)
            bitmap.recycle()
            out.toByteArray()
        }
    }

    private const val SEED = 2026
}
