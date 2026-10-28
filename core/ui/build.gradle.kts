plugins {
    alias(libs.plugins.performance.android.library.compose)
}

android {
    namespace = "com.danyastry.performance.core.ui"
}

dependencies {
    api(libs.androidx.material3)
}
