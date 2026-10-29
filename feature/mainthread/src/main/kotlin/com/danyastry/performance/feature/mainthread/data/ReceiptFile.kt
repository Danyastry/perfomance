package com.danyastry.performance.feature.mainthread.data

import com.danyastry.performance.feature.mainthread.logic.ReceiptGenerator
import java.io.File

class ReceiptFile(private val directory: File, private val lines: Int = LINES) {

    val file: File
        get() = File(directory, "receipt-$lines.csv")

    fun ensureWritten(): File {
        val target = file
        if (!target.exists()) {
            val temp = File(directory, target.name + ".tmp")
            temp.writeText(ReceiptGenerator.text(lines))
            check(temp.renameTo(target)) { "Could not move ${temp.name} into place" }
        }
        return target
    }

    fun readBytes(): ByteArray = ensureWritten().readBytes()

    companion object {
        const val LINES = 120_000
    }
}
