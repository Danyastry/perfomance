package com.danyastry.performance.feature.snapshots.logic

import com.danyastry.performance.feature.snapshots.model.DemoStep
import com.danyastry.performance.feature.snapshots.model.SnapshotDemo

object SnapshotDemos {

    fun run(demo: SnapshotDemo): List<DemoStep> = when (demo) {
        SnapshotDemo.ISOLATION -> IsolationDemo.run()
        SnapshotDemo.CONFLICT -> ConflictDemo.withDefaultPolicy()
        SnapshotDemo.MERGE -> ConflictDemo.withMergePolicy()
        SnapshotDemo.OBSERVATION -> ObservationDemo.run()
    }
}
