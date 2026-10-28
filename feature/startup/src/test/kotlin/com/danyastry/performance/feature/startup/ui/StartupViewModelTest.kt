package com.danyastry.performance.feature.startup.ui

import com.danyastry.performance.feature.startup.data.StartupStore
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.ProfileStatus
import com.danyastry.performance.feature.startup.model.StartupSample
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StartupViewModelTest {

    private val store = FakeStore()
    private val lastLaunch = MutableStateFlow<StartupSample?>(null)
    private val library = FakeLibrary()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `shows the launch the tracker recorded and the stats`() = runTest {
        store.samples += StartupSample(InitMode.EAGER, 100, 900, 0)
        val viewModel = viewModel()

        val sample = StartupSample(InitMode.EAGER, 120, 800, 1)
        store.samples += sample
        lastLaunch.value = sample

        assertThat(viewModel.state.value.lastLaunch).isEqualTo(sample)
        assertThat(viewModel.state.value.stats.single().medianFirstFrameMillis).isEqualTo(850)
    }

    @Test
    fun `a new mode is saved and asks for a restart`() = runTest {
        lastLaunch.value = StartupSample(InitMode.EAGER, 1, 2, 3)
        val viewModel = viewModel()

        viewModel.onModeChange(InitMode.LAZY)

        assertThat(store.mode).isEqualTo(InitMode.LAZY)
        assertThat(viewModel.state.value.restartNeeded).isTrue()
    }

    @Test
    fun `first use of a lazy library reports its cost`() = runTest {
        var now = 0L
        val viewModel = StartupViewModel(store, lastLaunch, library, StandardTestDispatcher(testScheduler), clock = { now })
        library.onInitialize = { now += 250_000_000 }

        viewModel.onUseLibrary()
        advanceUntilIdle()

        assertThat(viewModel.state.value.firstUseMillis).isEqualTo(250)
        assertThat(viewModel.state.value.libraryReady).isTrue()

        viewModel.onUseLibrary()
        advanceUntilIdle()
        assertThat(viewModel.state.value.firstUseMillis).isEqualTo(0)
    }

    @Test
    fun `shows the profile status it reads`() = runTest {
        val viewModel = StartupViewModel(store, lastLaunch, library, UnconfinedTestDispatcher(), profileStatus = { ProfileStatus.COMPILED })

        assertThat(viewModel.state.value.profileStatus).isEqualTo(ProfileStatus.COMPILED)
    }

    @Test
    fun `clears the history`() = runTest {
        store.samples += StartupSample(InitMode.EAGER, 100, 900, 0)
        val viewModel = viewModel()

        viewModel.onClearHistory()

        assertThat(viewModel.state.value.stats).isEmpty()
        assertThat(store.samples).isEmpty()
    }

    private fun viewModel() = StartupViewModel(store, lastLaunch, library, UnconfinedTestDispatcher())

    private class FakeStore : StartupStore {
        val samples = mutableListOf<StartupSample>()
        override var mode: InitMode = InitMode.EAGER

        override fun samples(): List<StartupSample> = samples.toList()

        override fun add(sample: StartupSample) {
            samples += sample
        }

        override fun clear() = samples.clear()
    }

    private class FakeLibrary : StartupLibrary {
        var ready = false
        var onInitialize: () -> Unit = {}

        override fun isReady() = ready

        override fun initialize() {
            if (!ready) onInitialize()
            ready = true
        }
    }
}
