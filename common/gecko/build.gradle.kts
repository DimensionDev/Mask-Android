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
                implementation("androidx.core:core-ktx:${Versions.Androidx.core}")
                implementation("androidx.appcompat:appcompat:${Versions.Androidx.appcompat}")
                implementation("androidx.activity:activity-ktx:${Versions.Androidx.activity}")
                implementation("androidx.fragment:fragment-ktx:${Versions.Androidx.fragment}")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
                implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
                implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}")

                implementation("org.jetbrains.compose.ui:ui:${Versions.compose_jb}")

                implementation("org.mozilla.components:concept-engine:${Versions.mozilla_components}")
                implementation("org.mozilla.components:browser-engine-gecko:${Versions.mozilla_components}")
                implementation("org.mozilla.components:browser-state:${Versions.mozilla_components}")
                implementation("org.mozilla.components:feature-tabs:${Versions.mozilla_components}")
                implementation("org.mozilla.components:feature-session:${Versions.mozilla_components}")
                implementation("org.mozilla.components:feature-prompts:${Versions.mozilla_components}")
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
