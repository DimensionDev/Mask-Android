plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
    id("org.jetbrains.compose")
}

android {
    setup()
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:${Versions.Androidx.activityCompose}")
    implementation("org.jetbrains.compose.ui:ui:${Versions.compose_jb}")
    implementation("org.jetbrains.compose.ui:ui-util:${Versions.compose_jb}")
    implementation("org.jetbrains.compose.foundation:foundation:${Versions.compose_jb}")
    implementation("org.jetbrains.compose.material:material:${Versions.compose_jb}")
    implementation(projects.common.gecko)
}
