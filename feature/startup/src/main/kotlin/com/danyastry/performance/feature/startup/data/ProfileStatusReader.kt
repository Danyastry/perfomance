package com.danyastry.performance.feature.startup.data

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.profileinstaller.ProfileVerifier
import com.danyastry.performance.feature.startup.logic.ProfileStatusMapper
import com.danyastry.performance.feature.startup.model.ProfileStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileStatusReader(context: Context) {

    private val debuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    suspend fun read(): ProfileStatus = withContext(Dispatchers.IO) {
        runCatching {
            val status = ProfileVerifier.getCompilationStatusAsync().get()
            val installed = status.profileInstallResultCode == ProfileVerifier.CompilationStatus.RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION ||
                status.profileInstallResultCode == ProfileVerifier.CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE ||
                status.profileInstallResultCode == ProfileVerifier.CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING
            ProfileStatusMapper.map(
                debuggable = debuggable,
                compiledWithProfile = status.isCompiledWithProfile,
                enqueuedForCompilation = status.hasProfileEnqueuedForCompilation(),
                installedOrPresent = installed,
            )
        }.getOrDefault(ProfileStatus.UNKNOWN)
    }
}
