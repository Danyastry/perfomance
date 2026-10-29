package com.danyastry.performance.feature.mainthread

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry
import com.danyastry.performance.core.ui.FeatureInfo
import com.danyastry.performance.feature.mainthread.ui.MainThreadRoute

object MainThreadFeature : FeatureEntry {

    override val info = FeatureInfo(
        id = "main-thread",
        name = "Main thread",
        category = FeatureCategory.MEMORY,
        summary = "The same receipt load on and off the main thread, with StrictMode, frame gaps and trace sections",
        minSdk = Build.VERSION_CODES.P,
    )

    @Composable
    override fun Content(modifier: Modifier) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) MainThreadRoute(modifier = modifier)
    }
}
