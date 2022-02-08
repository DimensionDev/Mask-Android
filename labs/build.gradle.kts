plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose").version(Versions.compose_jb)
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(projects.common)
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
