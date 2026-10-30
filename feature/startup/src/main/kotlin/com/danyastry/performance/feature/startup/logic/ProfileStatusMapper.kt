package com.danyastry.performance.feature.startup.logic

import androidx.profileinstaller.ProfileVerifier.CompilationStatus
import com.danyastry.performance.feature.startup.model.ProfileStatus

object ProfileStatusMapper {

    private val INSTALLED_RESULTS = setOf(
        CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE,
        CompilationStatus.RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION,
        CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING,
    )

    fun map(debuggable: Boolean, resultCode: Int, compiledWithProfile: Boolean, enqueuedForCompilation: Boolean): ProfileStatus = when {
        debuggable -> ProfileStatus.DEBUGGABLE
        compiledWithProfile -> ProfileStatus.COMPILED
        enqueuedForCompilation || resultCode in INSTALLED_RESULTS -> ProfileStatus.AWAITING_COMPILATION
        else -> ProfileStatus.NO_PROFILE
    }
}
