plugins {
    alias(libs.plugins.performance.android.feature)
}

android {
    namespace = "com.danyastry.performance.feature.mainthread"
}

dependencies {
    implementation(libs.androidx.tracing.ktx)
}
