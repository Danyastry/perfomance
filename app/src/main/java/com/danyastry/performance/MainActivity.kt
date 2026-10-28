package com.danyastry.performance

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.danyastry.performance.catalog.FeatureCatalog
import com.danyastry.performance.catalog.FeatureRegistry
import com.danyastry.performance.core.ui.theme.PerformanceTheme
import com.danyastry.performance.feature.startup.tracking.StartupTracker
import com.danyastry.performance.navigation.PerformanceNavHost

class MainActivity : ComponentActivity() {

    private val catalog = FeatureCatalog(FeatureRegistry.features, deviceSdk = Build.VERSION.SDK_INT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StartupTracker.onActivityCreate(this)
        enableEdgeToEdge()
        setContent {
            PerformanceTheme {
                PerformanceNavHost(catalog)
            }
        }
    }
}
