plugins {
    alias(libs.plugins.performance.android.feature)
}

android {
    namespace = "com.danyastry.performance.feature.rendering"
}

dependencies {
    implementation(projects.core.contract)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.ui.test.manifest)
}
