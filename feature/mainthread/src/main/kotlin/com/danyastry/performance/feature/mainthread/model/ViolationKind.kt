package com.danyastry.performance.feature.mainthread.model

enum class ViolationKind {
    DISK_READ,
    DISK_WRITE,
    SLOW_CALL,
    NETWORK,
    OTHER,
}
