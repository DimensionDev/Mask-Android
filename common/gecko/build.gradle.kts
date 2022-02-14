plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation("org.mozilla.geckoview:geckoview:${Versions.gecko}")
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
