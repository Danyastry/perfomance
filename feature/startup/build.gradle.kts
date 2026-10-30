plugins {
    alias(libs.plugins.performance.android.feature)
}

android {
    namespace = "com.danyastry.performance.feature.startup"
}

dependencies {
    implementation(projects.core.contract)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.profileinstaller)
}
