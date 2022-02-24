plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.ui:ui:${Versions.compose_jb}")
                implementation("org.mozilla.components:concept-engine:${Versions.mozilla_components}")
                implementation("org.mozilla.components:browser-engine-gecko:${Versions.mozilla_components}")
                implementation("org.mozilla.components:browser-state:${Versions.mozilla_components}")
                implementation("org.mozilla.components:feature-tabs:${Versions.mozilla_components}")
                implementation("org.mozilla.components:feature-sessions:${Versions.mozilla_components}")
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
