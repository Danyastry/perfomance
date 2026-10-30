package com.danyastry.performance.feature.runtime.logic

object Crc32Workload {

    private const val POLYNOMIAL = 0xEDB88320.toInt()

    private val TABLE = IntArray(256) { entry ->
        var crc = entry
        repeat(8) { crc = if (crc and 1 != 0) (crc ushr 1) xor POLYNOMIAL else crc ushr 1 }
        crc
    }

    fun crc32(data: ByteArray): Long {
        var crc = -1
        for (byte in data) crc = TABLE[(crc xor byte.toInt()) and 0xFF] xor (crc ushr 8)
        return crc.inv().toLong() and 0xFFFFFFFFL
    }

    fun input(size: Int): ByteArray = ByteArray(size) { (it * 31 + 7).toByte() }
}
