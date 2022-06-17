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
                implementation("com.fasterxml.jackson.core:jackson-core:2.13.3")
                implementation("org.msgpack:jackson-dataformat-msgpack:0.9.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.coroutines}")
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
        val androidAndroidTest by getting {
            dependencies {
                implementation("androidx.arch.core:core-testing:2.1.0")
                implementation("androidx.test:core:1.4.0")
                implementation("androidx.test:runner:1.4.0")
                implementation("androidx.test.ext:junit-ktx:1.1.3")
                implementation("androidx.test.espresso:espresso-core:3.4.0")
            }
        }
    }
}

android {
    setupLibrary()
}
