package com.danyastry.performance.feature.mainthread.logic

import com.danyastry.performance.feature.mainthread.model.ViolationEntry
import com.danyastry.performance.feature.mainthread.model.ViolationGroup

object ViolationGroups {

    fun of(violations: List<ViolationEntry>): List<ViolationGroup> =
        violations.groupBy { Triple(it.kind, it.origin, it.thread) }
            .map { (key, entries) -> ViolationGroup(key.first, key.second, key.third, entries.size) }
}
