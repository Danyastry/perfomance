package com.danyastry.performance.feature.startup.logic

import com.danyastry.performance.feature.startup.model.ProfileStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProfileStatusMapperTest {

    @Test
    fun `debuggable builds never use profiles`() {
        assertThat(ProfileStatusMapper.map(debuggable = true, compiledWithProfile = true, enqueuedForCompilation = true, installedOrPresent = true))
            .isEqualTo(ProfileStatus.DEBUGGABLE)
    }

    @Test
    fun `reports compiled, waiting and missing profiles`() {
        assertThat(ProfileStatusMapper.map(false, compiledWithProfile = true, enqueuedForCompilation = false, installedOrPresent = true))
            .isEqualTo(ProfileStatus.COMPILED)
        assertThat(ProfileStatusMapper.map(false, compiledWithProfile = false, enqueuedForCompilation = true, installedOrPresent = false))
            .isEqualTo(ProfileStatus.AWAITING_COMPILATION)
        assertThat(ProfileStatusMapper.map(false, compiledWithProfile = false, enqueuedForCompilation = false, installedOrPresent = false))
            .isEqualTo(ProfileStatus.NO_PROFILE)
    }
}
