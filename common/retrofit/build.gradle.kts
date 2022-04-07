plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
                api(projects.common.okhttp)
                api("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
                api("com.squareup.retrofit2:converter-scalars:${Versions.retrofit}")
                api("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.retrofitSerialization}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Kotlin.serialization}")
            }
        }
        val androidTest by getting {
            dependencies {
            }
        }
    }
}

android {
    setupLibrary()
}
