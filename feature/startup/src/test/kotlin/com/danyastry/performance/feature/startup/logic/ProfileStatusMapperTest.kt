package com.danyastry.performance.feature.startup.logic

import androidx.profileinstaller.ProfileVerifier.CompilationStatus
import com.danyastry.performance.feature.startup.model.ProfileStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProfileStatusMapperTest {

    @Test
    fun `debuggable builds never use profiles`() {
        assertThat(map(CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE, debuggable = true, compiled = true, enqueued = true))
            .isEqualTo(ProfileStatus.DEBUGGABLE)
    }

    @Test
    fun `a reference profile means the app was compiled with it`() {
        assertThat(map(CompilationStatus.RESULT_CODE_NO_PROFILE_INSTALLED, compiled = true)).isEqualTo(ProfileStatus.COMPILED)
    }

    @Test
    fun `a current profile waiting for dexopt is awaiting compilation`() {
        assertThat(map(CompilationStatus.RESULT_CODE_NO_PROFILE_INSTALLED, enqueued = true)).isEqualTo(ProfileStatus.AWAITING_COMPILATION)
    }

    @Test
    fun `each install result code without compiled or enqueued flags`() {
        val expected = mapOf(
            CompilationStatus.RESULT_CODE_NO_PROFILE_INSTALLED to ProfileStatus.NO_PROFILE,
            CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE to ProfileStatus.AWAITING_COMPILATION,
            CompilationStatus.RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION to ProfileStatus.AWAITING_COMPILATION,
            CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING to ProfileStatus.AWAITING_COMPILATION,
            CompilationStatus.RESULT_CODE_ERROR_PACKAGE_NAME_DOES_NOT_EXIST to ProfileStatus.NO_PROFILE,
            CompilationStatus.RESULT_CODE_ERROR_CACHE_FILE_EXISTS_BUT_CANNOT_BE_READ to ProfileStatus.NO_PROFILE,
            CompilationStatus.RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE to ProfileStatus.NO_PROFILE,
            CompilationStatus.RESULT_CODE_ERROR_UNSUPPORTED_API_VERSION to ProfileStatus.NO_PROFILE,
            CompilationStatus.RESULT_CODE_ERROR_NO_PROFILE_EMBEDDED to ProfileStatus.NO_PROFILE,
        )

        expected.forEach { (code, status) ->
            assertThat(map(code)).isEqualTo(status)
        }
    }

    private fun map(resultCode: Int, debuggable: Boolean = false, compiled: Boolean = false, enqueued: Boolean = false) =
        ProfileStatusMapper.map(debuggable, resultCode, compiled, enqueued)
}
