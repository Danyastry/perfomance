package com.danyastry.performance.feature.mainthread.logic

import com.danyastry.performance.feature.mainthread.model.ViolationEntry
import com.danyastry.performance.feature.mainthread.model.ViolationKind

object ViolationClassifier {

    fun entry(violationClass: String, thread: String, stack: Array<StackTraceElement>): ViolationEntry =
        ViolationEntry(kind = kind(violationClass), thread = thread, origin = origin(stack))

    fun kind(violationClass: String): ViolationKind = when (violationClass) {
        "DiskReadViolation" -> ViolationKind.DISK_READ
        "DiskWriteViolation" -> ViolationKind.DISK_WRITE
        "CustomViolation" -> ViolationKind.SLOW_CALL
        "NetworkViolation" -> ViolationKind.NETWORK
        else -> ViolationKind.OTHER
    }

    fun origin(stack: Array<StackTraceElement>): String? =
        stack.firstOrNull { it.className.startsWith(APP_PACKAGE) && !GENERATED_CLASS.containsMatchIn(it.className) }
            ?.let { "${it.className.substringAfterLast('.')}.${it.methodName.substringBefore(LAMBDA_MARKER)}" }

    private const val APP_PACKAGE = "com.danyastry.performance."
    private const val LAMBDA_MARKER = "\$lambda"
    private val GENERATED_CLASS = Regex("""\$(\d|.*Lambda)""")
}
