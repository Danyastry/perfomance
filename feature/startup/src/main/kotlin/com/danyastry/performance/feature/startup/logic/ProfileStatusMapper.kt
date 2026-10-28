package com.danyastry.performance.feature.startup.logic

import com.danyastry.performance.feature.startup.model.ProfileStatus

object ProfileStatusMapper {

    fun map(debuggable: Boolean, compiledWithProfile: Boolean, enqueuedForCompilation: Boolean, installedOrPresent: Boolean): ProfileStatus = when {
        debuggable -> ProfileStatus.DEBUGGABLE
        compiledWithProfile -> ProfileStatus.COMPILED
        enqueuedForCompilation -> ProfileStatus.AWAITING_COMPILATION
        installedOrPresent -> ProfileStatus.AWAITING_COMPILATION
        else -> ProfileStatus.NO_PROFILE
    }
}
