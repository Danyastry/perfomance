package com.danyastry.performance.feature.startup.tracking

import com.danyastry.performance.feature.startup.data.StartupLibrary
import com.danyastry.performance.feature.startup.data.StartupStore
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StartupTrackingTest {

    private val store = FakeStore()
    private val library = FakeLibrary()
    private val clocks = FakeClocks()
    private val background = mutableListOf<() -> Unit>()
    private val published = mutableListOf<StartupSample>()

    private val tracking = StartupTracking(store, library, clocks, runInBackground = { background += it }, onSample = { published += it })

    @Test
    fun `eager mode initializes the library before the application is marked created`() {
        store.mode = InitMode.EAGER
        library.onInitialize = { clocks.uptime += 300 }

        tracking.onApplicationCreate()
        val sample = tracking.onFirstDraw(drawnUptimeMillis = 1_600)

        assertThat(library.initializations).isEqualTo(1)
        assertThat(background).isEmpty()
        assertThat(sample).isEqualTo(StartupSample(InitMode.EAGER, applicationMillis = 400, firstFrameMillis = 600, timestampMillis = WALL_CLOCK))
    }

    @Test
    fun `background mode hands initialization to another thread`() {
        store.mode = InitMode.BACKGROUND

        tracking.onApplicationCreate()

        assertThat(library.initializations).isEqualTo(0)
        assertThat(background).hasSize(1)
        background.single().invoke()
        assertThat(library.initializations).isEqualTo(1)
    }

    @Test
    fun `lazy mode leaves the library alone`() {
        store.mode = InitMode.LAZY

        tracking.onApplicationCreate()

        assertThat(library.initializations).isEqualTo(0)
        assertThat(background).isEmpty()
    }

    @Test
    fun `only the first activity after application create is measured`() {
        assertThat(tracking.claimFirstActivity()).isFalse()

        tracking.onApplicationCreate()

        assertThat(tracking.claimFirstActivity()).isTrue()
        assertThat(tracking.claimFirstActivity()).isFalse()
    }

    @Test
    fun `a first draw is stored, published and tagged with the launch mode`() {
        store.mode = InitMode.LAZY
        tracking.onApplicationCreate()
        store.mode = InitMode.EAGER

        val sample = tracking.onFirstDraw(drawnUptimeMillis = 1_450)

        assertThat(sample!!.mode).isEqualTo(InitMode.LAZY)
        assertThat(store.samples()).containsExactly(sample)
        assertThat(published).containsExactly(sample)
    }

    @Test
    fun `refuses marks that are out of order`() {
        tracking.onApplicationCreate()
        assertThat(tracking.onFirstDraw(drawnUptimeMillis = 1_050)).isNull()

        clocks.processStart = 0
        assertThat(tracking.onFirstDraw(drawnUptimeMillis = 2_000)).isNull()

        clocks.processStart = 1_200
        assertThat(tracking.onFirstDraw(drawnUptimeMillis = 2_000)).isNull()

        assertThat(store.samples()).isEmpty()
        assertThat(published).isEmpty()
    }

    private class FakeClocks : StartupClocks {
        var processStart = 1_000L
        var uptime = 1_100L

        override fun processStartUptimeMillis() = processStart

        override fun uptimeMillis() = uptime

        override fun wallClockMillis() = WALL_CLOCK
    }

    private class FakeStore : StartupStore {
        private val samples = mutableListOf<StartupSample>()
        override var mode: InitMode = InitMode.EAGER

        override fun samples(): List<StartupSample> = samples.toList()

        override fun add(sample: StartupSample) {
            samples += sample
        }

        override fun clear() = samples.clear()
    }

    private class FakeLibrary : StartupLibrary {
        var initializations = 0
        var onInitialize: () -> Unit = {}

        override fun isReady() = initializations > 0

        override fun initialize() {
            onInitialize()
            initializations++
        }
    }

    private companion object {
        const val WALL_CLOCK = 1_700_000_000_000L
    }
}
