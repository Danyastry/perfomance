package com.danyastry.performance.feature.mainthread.ui

import androidx.annotation.StringRes
import com.danyastry.performance.feature.mainthread.R
import com.danyastry.performance.feature.mainthread.model.LoadMode
import com.danyastry.performance.feature.mainthread.model.ParserKind
import com.danyastry.performance.feature.mainthread.model.ViolationKind

object MainThreadLabels {

    @StringRes
    fun mode(mode: LoadMode): Int = when (mode) {
        LoadMode.MAIN_THREAD -> R.string.mainthread_mode_main
        LoadMode.BACKGROUND -> R.string.mainthread_mode_background
    }

    @StringRes
    fun explanation(mode: LoadMode): Int = when (mode) {
        LoadMode.MAIN_THREAD -> R.string.mainthread_main_explanation
        LoadMode.BACKGROUND -> R.string.mainthread_background_explanation
    }

    @StringRes
    fun parser(parser: ParserKind): Int = when (parser) {
        ParserKind.REGEX -> R.string.mainthread_parser_regex
        ParserKind.SPLIT -> R.string.mainthread_parser_split
        ParserKind.SCAN -> R.string.mainthread_parser_scan
    }

    @StringRes
    fun parserExplanation(parser: ParserKind): Int = when (parser) {
        ParserKind.REGEX -> R.string.mainthread_parser_regex_explanation
        ParserKind.SPLIT -> R.string.mainthread_parser_split_explanation
        ParserKind.SCAN -> R.string.mainthread_parser_scan_explanation
    }

    @StringRes
    fun violation(kind: ViolationKind): Int = when (kind) {
        ViolationKind.DISK_READ -> R.string.mainthread_violation_disk_read
        ViolationKind.DISK_WRITE -> R.string.mainthread_violation_disk_write
        ViolationKind.SLOW_CALL -> R.string.mainthread_violation_slow_call
        ViolationKind.NETWORK -> R.string.mainthread_violation_network
        ViolationKind.OTHER -> R.string.mainthread_violation_other
    }
}
