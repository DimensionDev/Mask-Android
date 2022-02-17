plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
                api("com.squareup.okhttp3:okhttp:${Versions.okhttp}")
                api("com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
    }
}

android {
    setupLibrary()
}
