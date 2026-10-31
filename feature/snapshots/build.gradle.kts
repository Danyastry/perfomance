plugins {
    alias(libs.plugins.performance.android.feature)
}

android {
    namespace = "com.danyastry.performance.feature.snapshots"
}

dependencies {
    implementation(projects.core.contract)
}
