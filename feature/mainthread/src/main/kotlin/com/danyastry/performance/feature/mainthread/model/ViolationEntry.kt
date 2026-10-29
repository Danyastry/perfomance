package com.danyastry.performance.feature.mainthread.model

data class ViolationEntry(
    val kind: ViolationKind,
    val thread: String,
    val origin: String?,
)
