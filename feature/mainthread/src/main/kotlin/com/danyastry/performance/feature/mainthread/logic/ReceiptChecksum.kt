package com.danyastry.performance.feature.mainthread.logic

import java.security.MessageDigest

object ReceiptChecksum {

    fun of(bytes: ByteArray, rounds: Int): String {
        require(rounds >= 1) { "rounds must be at least 1, was $rounds" }
        val digest = MessageDigest.getInstance("SHA-256")
        var result = digest.digest(bytes)
        repeat(rounds - 1) {
            digest.update(result)
            result = digest.digest(bytes)
        }
        return result.joinToString("") { "%02x".format(it) }
    }
}
