plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
    id("org.jetbrains.compose").version(Versions.compose_jb)
    id("com.google.devtools.ksp").version(Versions.ksp)
}

kotlin {
    android()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.routeProcessor.annotations)
                kspAndroid(projects.common.routeProcessor)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(projects.common)
                implementation(projects.common.gecko)
                implementation(projects.localization)
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
