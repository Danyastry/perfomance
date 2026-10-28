package com.danyastry.performance.feature.startup.ui

import androidx.annotation.StringRes
import com.danyastry.performance.feature.startup.R
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.ProfileStatus

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

@get:StringRes
val ProfileStatus.labelRes: Int
    get() = when (this) {
        ProfileStatus.COMPILED -> R.string.startup_profile_compiled
        ProfileStatus.AWAITING_COMPILATION -> R.string.startup_profile_awaiting
        ProfileStatus.DEBUGGABLE -> R.string.startup_profile_debuggable
        ProfileStatus.NO_PROFILE -> R.string.startup_profile_none
        ProfileStatus.UNKNOWN -> R.string.startup_profile_unknown
    }

@get:StringRes
val ProfileStatus.explanationRes: Int
    get() = when (this) {
        ProfileStatus.COMPILED -> R.string.startup_profile_compiled_explanation
        ProfileStatus.AWAITING_COMPILATION -> R.string.startup_profile_awaiting_explanation
        ProfileStatus.DEBUGGABLE -> R.string.startup_profile_debuggable_explanation
        ProfileStatus.NO_PROFILE -> R.string.startup_profile_none_explanation
        ProfileStatus.UNKNOWN -> R.string.startup_profile_unknown_explanation
    }
