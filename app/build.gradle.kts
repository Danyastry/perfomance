plugins {
    alias(libs.plugins.performance.android.application)
    alias(libs.plugins.performance.android.application.compose)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.danyastry.performance"

    defaultConfig {
        applicationId = "com.danyastry.performance"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.feature.startup)
    implementation(projects.feature.recomposition)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.profileinstaller)
    baselineProfile(projects.benchmark)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)
}
