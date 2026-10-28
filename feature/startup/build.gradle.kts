plugins {
    alias(libs.plugins.performance.android.feature)
}

android {
    namespace = "com.danyastry.performance.feature.startup"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
