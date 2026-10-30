plugins {
    alias(libs.plugins.performance.android.feature)
}

android {
    namespace = "com.danyastry.performance.feature.scrolling"
}

dependencies {
    implementation(projects.core.contract)
    implementation(libs.androidx.metrics.performance)
    implementation(libs.androidx.core.ktx)
}
