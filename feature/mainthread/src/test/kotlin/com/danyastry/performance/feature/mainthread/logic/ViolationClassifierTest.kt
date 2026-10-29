package com.danyastry.performance.feature.mainthread.logic

import com.danyastry.performance.feature.mainthread.model.ViolationKind
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ViolationClassifierTest {

    @Test
    fun `maps StrictMode violation classes to kinds`() {
        assertThat(ViolationClassifier.kind("DiskReadViolation")).isEqualTo(ViolationKind.DISK_READ)
        assertThat(ViolationClassifier.kind("DiskWriteViolation")).isEqualTo(ViolationKind.DISK_WRITE)
        assertThat(ViolationClassifier.kind("CustomViolation")).isEqualTo(ViolationKind.SLOW_CALL)
        assertThat(ViolationClassifier.kind("NetworkViolation")).isEqualTo(ViolationKind.NETWORK)
        assertThat(ViolationClassifier.kind("ResourceMismatchViolation")).isEqualTo(ViolationKind.OTHER)
    }

    @Test
    fun `origin is the first frame from the app`() {
        val stack = arrayOf(
            StackTraceElement("android.os.StrictMode\$AndroidBlockGuardPolicy", "onReadFromDisk", null, 1),
            StackTraceElement("java.io.FileInputStream", "<init>", null, 2),
            StackTraceElement("com.danyastry.performance.feature.mainthread.data.ReceiptFile", "readBytes", null, 3),
            StackTraceElement("com.danyastry.performance.feature.mainthread.logic.ReceiptLoader", "load", null, 4),
        )

        val entry = ViolationClassifier.entry("DiskReadViolation", "main", stack)

        assertThat(entry.origin).isEqualTo("ReceiptFile.readBytes")
        assertThat(entry.thread).isEqualTo("main")
    }

    @Test
    fun `origin skips generated lambda and function reference classes`() {
        val stack = arrayOf(
            StackTraceElement("com.danyastry.performance.feature.mainthread.logic.ReceiptLoader\$1", "invoke", null, 1),
            StackTraceElement("com.danyastry.performance.feature.mainthread.logic.ReceiptLoader\$\$ExternalSyntheticLambda0", "invoke", null, 2),
            StackTraceElement("com.danyastry.performance.feature.mainthread.logic.ReceiptLoader", "load", null, 3),
        )

        assertThat(ViolationClassifier.origin(stack)).isEqualTo("ReceiptLoader.load")
    }

    @Test
    fun `origin names the function that holds a compiled lambda`() {
        val stack = arrayOf(
            StackTraceElement("com.danyastry.performance.feature.mainthread.ui.MainThreadScreenKt", "MainThreadRoute\$lambda\$12\$lambda\$8", null, 1),
        )

        assertThat(ViolationClassifier.origin(stack)).isEqualTo("MainThreadScreenKt.MainThreadRoute")
    }

    @Test
    fun `origin keeps named nested classes`() {
        val stack = arrayOf(StackTraceElement("com.danyastry.performance.feature.mainthread.data.ReceiptFile\$Companion", "create", null, 1))

        assertThat(ViolationClassifier.origin(stack)).isEqualTo("ReceiptFile\$Companion.create")
    }

    @Test
    fun `origin is unknown when no app frame is on the stack`() {
        val stack = arrayOf(StackTraceElement("java.io.FileInputStream", "<init>", null, 1))

        assertThat(ViolationClassifier.origin(stack)).isNull()
    }
}
