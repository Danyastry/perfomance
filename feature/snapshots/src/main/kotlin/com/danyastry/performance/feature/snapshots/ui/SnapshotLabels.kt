package com.danyastry.performance.feature.snapshots.ui

import androidx.annotation.StringRes
import com.danyastry.performance.feature.snapshots.R
import com.danyastry.performance.feature.snapshots.model.SnapshotDemo

object SnapshotLabels {

    @StringRes
    fun title(demo: SnapshotDemo): Int = when (demo) {
        SnapshotDemo.ISOLATION -> R.string.snapshots_isolation_title
        SnapshotDemo.CONFLICT -> R.string.snapshots_conflict_title
        SnapshotDemo.MERGE -> R.string.snapshots_merge_title
        SnapshotDemo.OBSERVATION -> R.string.snapshots_observation_title
    }

    @StringRes
    fun explanation(demo: SnapshotDemo): Int = when (demo) {
        SnapshotDemo.ISOLATION -> R.string.snapshots_isolation_explanation
        SnapshotDemo.CONFLICT -> R.string.snapshots_conflict_explanation
        SnapshotDemo.MERGE -> R.string.snapshots_merge_explanation
        SnapshotDemo.OBSERVATION -> R.string.snapshots_observation_explanation
    }
}
