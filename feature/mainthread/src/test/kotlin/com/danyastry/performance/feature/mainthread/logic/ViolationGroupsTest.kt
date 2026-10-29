package com.danyastry.performance.feature.mainthread.logic

import com.danyastry.performance.feature.mainthread.model.ViolationEntry
import com.danyastry.performance.feature.mainthread.model.ViolationGroup
import com.danyastry.performance.feature.mainthread.model.ViolationKind
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ViolationGroupsTest {

    @Test
    fun `groups repeats and keeps the order of first appearance`() {
        val slow = ViolationEntry(ViolationKind.SLOW_CALL, "main", "ReceiptLoader.load")
        val read = ViolationEntry(ViolationKind.DISK_READ, "main", "ReceiptFile.readBytes")
        val write = ViolationEntry(ViolationKind.DISK_WRITE, "main", "ReceiptFile.ensureWritten")

        val groups = ViolationGroups.of(listOf(slow, read, read, write, read, write))

        assertThat(groups).containsExactly(
            ViolationGroup(ViolationKind.SLOW_CALL, "ReceiptLoader.load", "main", 1),
            ViolationGroup(ViolationKind.DISK_READ, "ReceiptFile.readBytes", "main", 3),
            ViolationGroup(ViolationKind.DISK_WRITE, "ReceiptFile.ensureWritten", "main", 2),
        ).inOrder()
    }

    @Test
    fun `the same kind from different code stays separate`() {
        val groups = ViolationGroups.of(
            listOf(
                ViolationEntry(ViolationKind.DISK_READ, "main", "ReceiptFile.readBytes"),
                ViolationEntry(ViolationKind.DISK_READ, "main", null),
            ),
        )

        assertThat(groups.map { it.count }).containsExactly(1, 1)
    }
}
