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
                api(projects.wallet.export)
                api(projects.labs.export)
                api(projects.persona.export)
                api(projects.setting.export)
                api("org.jetbrains.compose.ui:ui:${Versions.compose_jb}")
                api("org.jetbrains.compose.ui:ui-util:${Versions.compose_jb}")
                api("org.jetbrains.compose.foundation:foundation:${Versions.compose_jb}")
                api("org.jetbrains.compose.material:material:${Versions.compose_jb}")
                api("org.jetbrains.compose.material:material-icons-core:${Versions.compose_jb}")
                api("org.jetbrains.compose.material:material-icons-extended:${Versions.compose_jb}")
                api("org.jetbrains.compose.ui:ui-tooling:${Versions.compose_jb}")
                api("io.insert-koin:koin-android:${Versions.koin}")
                // api("io.insert-koin:koin-android-viewmodel:${Versions.koin}")
                api("io.insert-koin:koin-androidx-compose:${Versions.koin}")
                api("androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
                implementation("io.coil-kt:coil-compose:${Versions.coil}")
                implementation("io.coil-kt:coil-svg:${Versions.coil}")
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
