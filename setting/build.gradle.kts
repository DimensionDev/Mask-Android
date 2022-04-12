plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose").version(Versions.compose_jb)
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
    id("com.google.devtools.ksp").version(Versions.ksp)
}

kotlin {
    android()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.routeProcessor.annotations)
                kspAndroid(projects.common.routeProcessor)
                implementation("com.ensarsarajcic.kotlinx:serialization-msgpack:${Versions.kotlinxSerializationMsgPackVersion}")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(projects.common)
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
