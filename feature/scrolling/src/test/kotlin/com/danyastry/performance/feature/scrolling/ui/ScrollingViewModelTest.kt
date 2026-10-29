package com.danyastry.performance.feature.scrolling.ui

import com.danyastry.performance.feature.scrolling.model.FeedItem
import com.danyastry.performance.feature.scrolling.model.ListMode
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScrollingViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `prepares every product row off the main thread`() = runTest {
        val viewModel = ScrollingViewModel(UnconfinedTestDispatcher(testScheduler), loadImage = { ByteArray(3) })

        val products = viewModel.items.filterIsInstance<FeedItem.Product>()
        assertThat(viewModel.models.value.keys).containsExactlyElementsIn(products.map { it.id })
    }

    @Test
    fun `changing mode resets the jank meter`() = runTest {
        val viewModel = ScrollingViewModel(UnconfinedTestDispatcher(testScheduler), loadImage = { ByteArray(3) })
        viewModel.counter.record(40_000_000, isJank = true)

        viewModel.onModeChange(ListMode.OPTIMIZED)

        assertThat(viewModel.mode.value).isEqualTo(ListMode.OPTIMIZED)
        assertThat(viewModel.counter.summary.value.frames).isEqualTo(0)
    }
}
