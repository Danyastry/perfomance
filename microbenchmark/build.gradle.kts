plugins {
    alias(libs.plugins.performance.android.library)
    alias(libs.plugins.androidx.benchmark)
}

android {
    namespace = "com.danyastry.performance.microbenchmark"

    defaultConfig {
        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    testBuildType = "release"
    buildTypes {
        release {
            isDefault = true
        }
    }
}

dependencies {
    androidTestImplementation(projects.feature.mainthread)
    androidTestImplementation(libs.androidx.benchmark.junit4)
}
