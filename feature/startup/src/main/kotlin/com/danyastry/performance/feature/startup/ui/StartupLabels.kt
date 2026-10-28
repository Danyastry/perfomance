package com.danyastry.performance.feature.startup.ui

import androidx.annotation.StringRes
import com.danyastry.performance.feature.startup.R
import com.danyastry.performance.feature.startup.model.InitMode

@get:StringRes
val InitMode.labelRes: Int
    get() = when (this) {
        InitMode.EAGER -> R.string.startup_mode_eager
        InitMode.LAZY -> R.string.startup_mode_lazy
        InitMode.BACKGROUND -> R.string.startup_mode_background
    }

@get:StringRes
val InitMode.explanationRes: Int
    get() = when (this) {
        InitMode.EAGER -> R.string.startup_mode_eager_explanation
        InitMode.LAZY -> R.string.startup_mode_lazy_explanation
        InitMode.BACKGROUND -> R.string.startup_mode_background_explanation
    }
