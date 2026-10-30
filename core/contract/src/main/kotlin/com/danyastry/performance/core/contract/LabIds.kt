package com.danyastry.performance.core.contract

object LabIds {
    const val COLD_START = "cold-start"
    const val RECOMPOSITION = "recomposition"
    const val LIST_SCROLLING = "list-scrolling"
    const val MEMORY_LEAKS = "memory-leaks"
    const val MAIN_THREAD = "main-thread"
    const val JIT_WARMUP = "jit-warmup"
    const val GARBAGE_COLLECTION = "garbage-collection"
    const val FRAME_PIPELINE = "frame-pipeline"

    val ALL: List<String> = listOf(
        COLD_START,
        RECOMPOSITION,
        LIST_SCROLLING,
        MEMORY_LEAKS,
        MAIN_THREAD,
        JIT_WARMUP,
        GARBAGE_COLLECTION,
        FRAME_PIPELINE,
    )
}
