package com.danyastry.performance.feature.memory.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableLongStateOf
import com.danyastry.performance.core.ui.theme.PerformanceTheme
import com.danyastry.performance.feature.memory.R
import com.danyastry.performance.feature.memory.leak.PriceListener
import com.danyastry.performance.feature.memory.leak.PriceUpdates
import com.danyastry.performance.feature.memory.logic.PriceTicker
import com.danyastry.performance.feature.memory.ui.PriceScreenContent

class FixedActivity : ComponentActivity(), PriceListener {

    private val cents = mutableLongStateOf(0L)
    private val screenBuffer = ByteArray(BUFFER_BYTES)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PerformanceTheme {
                PriceScreenContent(
                    title = getString(R.string.memory_fixed_title),
                    explanation = getString(R.string.memory_fixed_explanation, screenBuffer.size / BYTES_PER_MB),
                    cents = cents.longValue,
                    onPublish = { PriceUpdates.publish(PriceTicker.next()) },
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        PriceUpdates.register(this)
    }

    override fun onStop() {
        PriceUpdates.unregister(this)
        super.onStop()
    }

    override fun onPrice(cents: Long) {
        this.cents.longValue = cents
    }

    private companion object {
        const val BYTES_PER_MB = 1024 * 1024
        const val BUFFER_BYTES = 8 * BYTES_PER_MB
    }
}
