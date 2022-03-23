plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose").version(Versions.compose_jb)
    id("com.google.devtools.ksp").version(Versions.ksp)
}

kotlin {
    android()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.common)
                api(projects.wallet)
                api(projects.persona)
                api(projects.labs)
                api(projects.setting)
                api(projects.extension)
                implementation(projects.common.routeProcessor.annotations)
                kspAndroid(projects.common.routeProcessor)
            }
        }

        val androidMain by getting {
            dependencies {
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
