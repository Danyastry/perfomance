package com.danyastry.performance.feature.mainthread.model

data class ViolationGroup(
    val kind: ViolationKind,
    val origin: String?,
    val thread: String,
    val count: Int,
)
