plugins {
    alias(libs.plugins.performance.android.feature)
}

android {
    namespace = "com.danyastry.performance.feature.memory"
}

dependencies {
    implementation(libs.androidx.activity.compose)
}
