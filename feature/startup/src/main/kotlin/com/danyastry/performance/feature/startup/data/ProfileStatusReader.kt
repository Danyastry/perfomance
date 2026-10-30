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
            ProfileStatusMapper.map(
                debuggable = debuggable,
                resultCode = status.profileInstallResultCode,
                compiledWithProfile = status.isCompiledWithProfile,
                enqueuedForCompilation = status.hasProfileEnqueuedForCompilation(),
            )
        }.getOrDefault(ProfileStatus.UNKNOWN)
    }
}
