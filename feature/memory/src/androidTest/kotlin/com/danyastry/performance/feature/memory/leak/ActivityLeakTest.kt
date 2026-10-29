package com.danyastry.performance.feature.memory.leak

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danyastry.performance.feature.memory.screen.FixedActivity
import com.danyastry.performance.feature.memory.screen.LeakyActivity
import com.google.common.truth.Truth.assertThat
import java.lang.ref.WeakReference
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityLeakTest {

    @Before
    fun setUp() = PriceUpdates.clear()

    @After
    fun tearDown() = PriceUpdates.clear()

    @Test
    fun leakyActivityIsRetainedAfterItIsDestroyed() {
        val activity = openAndClose(LeakyActivity::class.java)

        assertThat(isCollected(activity)).isFalse()
        assertThat(PriceUpdates.listenerCount).isEqualTo(1)
    }

    @Test
    fun theSingletonIsTheOnlyThingKeepingTheLeakyActivityAlive() {
        val activity = openAndClose(LeakyActivity::class.java)

        PriceUpdates.clear()

        assertThat(isCollected(activity)).isTrue()
    }

    @Test
    fun fixedActivityIsCollectedAfterItIsDestroyed() {
        val activity = openAndClose(FixedActivity::class.java)

        assertThat(PriceUpdates.listenerCount).isEqualTo(0)
        assertThat(isCollected(activity)).isTrue()
    }

    private fun <A : Activity> openAndClose(type: Class<A>): WeakReference<Activity> {
        lateinit var reference: WeakReference<Activity>
        ActivityScenario.launch(type).use { scenario ->
            scenario.onActivity { reference = WeakReference(it) }
        }
        return reference
    }

    private fun isCollected(reference: WeakReference<Activity>): Boolean {
        repeat(GC_ATTEMPTS) {
            if (isCleared(reference)) return true
            Runtime.getRuntime().gc()
            System.runFinalization()
            Thread.sleep(GC_PAUSE_MILLIS)
        }
        return isCleared(reference)
    }

    private fun isCleared(reference: WeakReference<Activity>): Boolean = reference.get() == null

    private companion object {
        const val GC_ATTEMPTS = 20
        const val GC_PAUSE_MILLIS = 100L
    }
}
