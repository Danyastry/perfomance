package com.danyastry.performance.feature.snapshots.logic

import androidx.compose.runtime.snapshots.SnapshotApplyResult

object SnapshotResults {

    fun name(result: SnapshotApplyResult): String = when (result) {
        is SnapshotApplyResult.Success -> "Success"
        is SnapshotApplyResult.Failure -> "Failure"
    }
}
