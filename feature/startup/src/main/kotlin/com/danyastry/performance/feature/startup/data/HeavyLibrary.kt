package com.danyastry.performance.feature.startup.data

import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicReference

object HeavyLibrary {

    private const val TABLE_SIZE = 150_000
    private const val DIGEST_ROUNDS = 4_000

    private val state = AtomicReference<String?>(null)

    val isReady: Boolean
        get() = state.get() != null

    @Synchronized
    fun initialize(): String {
        state.get()?.let { return it }
        val table = HashMap<Int, String>(TABLE_SIZE)
        for (i in 0 until TABLE_SIZE) table[i] = "entry-$i"
        val digest = MessageDigest.getInstance("SHA-256")
        var block = table.size.toString().toByteArray()
        repeat(DIGEST_ROUNDS) { block = digest.digest(block + table.getValue(it).toByteArray()) }
        val fingerprint = block.joinToString("") { "%02x".format(it) }.take(16)
        state.set(fingerprint)
        return fingerprint
    }

    fun fingerprintOrNull(): String? = state.get()
}
