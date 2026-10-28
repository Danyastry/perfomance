package com.danyastry.performance.feature.recomposition.tracking

object CounterIds {
    const val UNSTABLE_LIST = "stability.problem"
    const val IMMUTABLE_CLASS = "stability.fixed"
    const val EARLY_READ = "read.problem"
    const val LATE_READ = "read.fixed"
    const val PLAIN_CONDITION = "derived.problem"
    const val DERIVED_CONDITION = "derived.fixed"
    const val ROWS_WITHOUT_KEYS = "keys.problem"
    const val ROWS_WITH_KEYS = "keys.fixed"
}
