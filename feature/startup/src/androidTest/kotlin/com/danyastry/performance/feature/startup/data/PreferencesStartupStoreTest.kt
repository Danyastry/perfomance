package com.danyastry.performance.feature.startup.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesStartupStoreTest {

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() = wipe()

    @After
    fun tearDown() = wipe()

    @Test
    fun anEmptyStoreStartsEagerWithNoSamples() {
        val store = PreferencesStartupStore(context)

        assertThat(store.mode).isEqualTo(InitMode.EAGER)
        assertThat(store.samples()).isEmpty()
    }

    @Test
    fun modeAndSamplesSurviveANewInstance() {
        val first = PreferencesStartupStore(context)
        first.mode = InitMode.LAZY
        first.add(sample(1))
        first.add(sample(2))

        val second = PreferencesStartupStore(context)

        assertThat(second.mode).isEqualTo(InitMode.LAZY)
        assertThat(second.samples()).containsExactly(sample(1), sample(2)).inOrder()
    }

    @Test
    fun onlyTheNewestSamplesAreKept() {
        val store = PreferencesStartupStore(context)

        repeat(PreferencesStartupStore.MAX_SAMPLES + 5) { store.add(sample(it)) }

        val kept = store.samples()
        assertThat(kept).hasSize(PreferencesStartupStore.MAX_SAMPLES)
        assertThat(kept.first()).isEqualTo(sample(5))
        assertThat(kept.last()).isEqualTo(sample(PreferencesStartupStore.MAX_SAMPLES + 4))
    }

    @Test
    fun clearRemovesSamplesButKeepsTheMode() {
        val store = PreferencesStartupStore(context)
        store.mode = InitMode.BACKGROUND
        store.add(sample(1))

        store.clear()

        assertThat(store.samples()).isEmpty()
        assertThat(store.mode).isEqualTo(InitMode.BACKGROUND)
    }

    @Test
    fun anUnknownStoredModeFallsBackToEager() {
        preferences().edit().putString(PreferencesStartupStore.KEY_MODE, "TURBO").commit()

        assertThat(PreferencesStartupStore(context).mode).isEqualTo(InitMode.EAGER)
    }

    private fun sample(index: Int) = StartupSample(
        mode = InitMode.entries[index % InitMode.entries.size],
        applicationMillis = 100L + index,
        firstFrameMillis = 300L + index,
        timestampMillis = 1_700_000_000_000L + index,
    )

    private fun preferences() = context.getSharedPreferences(PreferencesStartupStore.FILE, Context.MODE_PRIVATE)

    private fun wipe() {
        preferences().edit().clear().commit()
    }
}
