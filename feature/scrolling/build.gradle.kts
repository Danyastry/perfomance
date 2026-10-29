plugins {
    alias(libs.plugins.performance.android.feature)
}

android {
    namespace = "com.danyastry.performance.feature.scrolling"
}

dependencies {
    implementation(libs.androidx.metrics.performance)
    implementation(libs.androidx.core.ktx)
}
