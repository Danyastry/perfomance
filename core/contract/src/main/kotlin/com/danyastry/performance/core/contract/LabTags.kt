package com.danyastry.performance.core.contract

object LabTags {
    const val CATALOG_LIST = "catalog_list"

    fun catalogRow(labId: String): String = "feature:$labId"

    const val COLD_START_RESTART = "startup_restart"

    const val SCROLLING_HEAVY = "scrolling_mode_heavy"
    const val SCROLLING_OPTIMIZED = "scrolling_mode_optimized"
    const val FEED_LIST = "feed_list"

    const val MAIN_THREAD_ON_MAIN = "mainthread_mode_main"
    const val MAIN_THREAD_IN_BACKGROUND = "mainthread_mode_background"
    const val PARSER_REGEX = "mainthread_parser_regex"
    const val PARSER_SPLIT = "mainthread_parser_split"
    const val PARSER_SCAN = "mainthread_parser_scan"
    const val MAIN_THREAD_LOAD = "mainthread_load"
    const val MAIN_THREAD_REPORT = "mainthread_report"
}
