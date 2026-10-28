package com.danyastry.performance.feature.recomposition.demo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danyastry.performance.feature.recomposition.model.ListItem
import com.danyastry.performance.feature.recomposition.tracking.CounterIds
import com.danyastry.performance.feature.recomposition.tracking.RecompositionCounters
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecompositionClaimsTest {

    @get:Rule
    val compose = createComposeRule()

    private val counters = RecompositionCounters()

    @Test
    fun aNewListInstanceRecomposesButAnEqualImmutableClassIsSkipped() {
        var tick by mutableIntStateOf(0)
        compose.setContent { StabilityDemo(tick, counters) }

        step(STEPS) { tick++ }

        assertThat(counters.count(CounterIds.UNSTABLE_LIST)).isEqualTo(1 + STEPS)
        assertThat(counters.count(CounterIds.IMMUTABLE_CLASS)).isEqualTo(1)
    }

    @Test
    fun readingStateInALayoutLambdaSkipsRecomposition() {
        var tick by mutableIntStateOf(0)
        compose.setContent { DeferredReadDemo({ tick }, counters) }

        step(STEPS) { tick++ }

        assertThat(counters.count(CounterIds.EARLY_READ)).isEqualTo(1 + STEPS)
        assertThat(counters.count(CounterIds.LATE_READ)).isEqualTo(1)
    }

    @Test
    fun derivedStateOfRecomposesOnlyWhenTheAnswerChanges() {
        var level by mutableIntStateOf(0)
        compose.setContent { DerivedStateDemo({ level }, counters) }

        val values = listOf(10, 20, 30, 60, 70, 80, 40, 45)
        values.forEach { value -> step(1) { level = value } }

        assertThat(counters.count(CounterIds.PLAIN_CONDITION)).isEqualTo(1 + values.size)
        assertThat(counters.count(CounterIds.DERIVED_CONDITION)).isEqualTo(1 + 2)
    }

    @Test
    fun keysLetExistingRowsSkipWhenAnItemIsInsertedAtTheTop() {
        val items = mutableStateListOf<ListItem>().apply { addAll((1..5).map { ListItem(it, "Item $it") }) }
        compose.setContent { KeysDemo(items, counters) }
        compose.waitForIdle()
        counters.reset()

        compose.runOnIdle { items.add(0, ListItem(6, "Item 6")) }
        compose.waitForIdle()

        assertThat(counters.count(CounterIds.ROWS_WITHOUT_KEYS)).isEqualTo(6)
        assertThat(counters.count(CounterIds.ROWS_WITH_KEYS)).isEqualTo(1)
    }

    private fun step(times: Int, change: () -> Unit) {
        compose.waitForIdle()
        repeat(times) {
            compose.runOnIdle(change)
            compose.waitForIdle()
        }
    }

    private companion object {
        const val STEPS = 10
    }
}
