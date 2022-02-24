plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
}

android {
    compileSdk = Versions.Android.compile
    buildToolsVersion = Versions.Android.buildTools
    defaultConfig {
        applicationId = "com.dimension.maskbook.common.gecko.sample"
        minSdk = Versions.Android.min
        targetSdk = Versions.Android.target
        versionCode = Package.versionCode
        versionName = Package.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
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
    implementation("com.google.android.material:material:${Versions.material}")
    implementation(projects.common.gecko)
}
