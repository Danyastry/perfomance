package com.danyastry.performance.feature.mainthread.logic.parse

class SkuSet(private val text: String, expected: Int = DEFAULT_EXPECTED) {

    private var starts = IntArray(capacityFor(expected))
    private var lengths = IntArray(starts.size).apply { fill(EMPTY) }
    private var hashes = IntArray(starts.size)

    var size: Int = 0
        private set

    fun add(start: Int, end: Int) {
        val length = end - start
        val hash = hash(start, end)
        val mask = starts.size - 1
        var slot = hash and mask
        while (lengths[slot] != EMPTY) {
            if (hashes[slot] == hash && lengths[slot] == length && text.regionMatches(starts[slot], text, start, length)) return
            slot = (slot + 1) and mask
        }
        starts[slot] = start
        lengths[slot] = length
        hashes[slot] = hash
        size++
        if (size * LOAD_DENOMINATOR >= starts.size * LOAD_NUMERATOR) grow()
    }

    private fun hash(start: Int, end: Int): Int {
        var hash = 0
        for (index in start until end) hash = HASH_PRIME * hash + text[index].code
        return hash xor (hash ushr 16)
    }

    private fun grow() {
        val oldStarts = starts
        val oldLengths = lengths
        val oldHashes = hashes
        starts = IntArray(oldStarts.size * 2)
        lengths = IntArray(starts.size).apply { fill(EMPTY) }
        hashes = IntArray(starts.size)
        val mask = starts.size - 1
        for (old in oldStarts.indices) {
            if (oldLengths[old] == EMPTY) continue
            var slot = oldHashes[old] and mask
            while (lengths[slot] != EMPTY) slot = (slot + 1) and mask
            starts[slot] = oldStarts[old]
            lengths[slot] = oldLengths[old]
            hashes[slot] = oldHashes[old]
        }
    }

    private companion object {
        const val EMPTY = -1
        const val HASH_PRIME = 31
        const val LOAD_NUMERATOR = 3
        const val LOAD_DENOMINATOR = 4
        const val DEFAULT_EXPECTED = 1_024

        fun capacityFor(expected: Int): Int {
            var capacity = MIN_CAPACITY
            while (capacity * LOAD_NUMERATOR <= expected * LOAD_DENOMINATOR) capacity *= 2
            return capacity
        }

        const val MIN_CAPACITY = 16
    }
}
