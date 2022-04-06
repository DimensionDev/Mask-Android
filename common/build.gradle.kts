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
                api(projects.common.bigDecimal)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api(projects.wallet.export)
                api(projects.labs.export)
                api(projects.persona.export)
                api(projects.setting.export)
                api(projects.extension.export)
                api(projects.localization)
                api(projects.common.retrofit)
                api(projects.common.okhttp)
                api(projects.common.bigDecimal)

                // Compose
                api("org.jetbrains.compose.ui:ui:${Versions.compose_jb}")
                api("org.jetbrains.compose.ui:ui-util:${Versions.compose_jb}")
                api("org.jetbrains.compose.foundation:foundation:${Versions.compose_jb}")
                api("org.jetbrains.compose.material:material:${Versions.compose_jb}")
                api("org.jetbrains.compose.material:material-icons-core:${Versions.compose_jb}")
                api("org.jetbrains.compose.material:material-icons-extended:${Versions.compose_jb}")
                api("org.jetbrains.compose.ui:ui-tooling:${Versions.compose_jb}")

                // Koin
                api("io.insert-koin:koin-android:${Versions.koin}")
                api("io.insert-koin:koin-androidx-compose:${Versions.koin}")

                // Lifecycle
                api("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}")
                api("androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}")

                // Coil
                api("io.coil-kt:coil-compose:${Versions.coil}")
                api("io.coil-kt:coil-svg:${Versions.coil}")

                // Accompanist
                api("com.google.accompanist:accompanist-pager:${Versions.accompanist}")
                api("com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}")
                api("com.google.accompanist:accompanist-swiperefresh:${Versions.accompanist}")
                api("com.google.accompanist:accompanist-navigation-animation:${Versions.accompanist}")
                api("com.google.accompanist:accompanist-navigation-material:${Versions.accompanist}")
                api("com.google.accompanist:accompanist-permissions:${Versions.accompanist}")
                api("com.google.accompanist:accompanist-insets:${Versions.accompanist}")

                // coroutines
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}")

                // Androidx
                api("androidx.core:core-ktx:${Versions.Androidx.core}")
                api("androidx.appcompat:appcompat:${Versions.Androidx.appcompat}")
                api("androidx.activity:activity-ktx:${Versions.Androidx.activity}")
                api("androidx.fragment:fragment-ktx:${Versions.Androidx.fragment}")
                api("androidx.datastore:datastore-preferences:${Versions.datastore}")
                api("androidx.navigation:navigation-ui-ktx:${Versions.navigation}")
                api("androidx.navigation:navigation-compose:${Versions.navigation}")
                implementation("androidx.biometric:biometric-ktx:${Versions.Androidx.biometric}")

                // serialization
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Kotlin.serialization}")

                // sqlite
                api("androidx.room:room-runtime:${Versions.Androidx.room}")
                api("androidx.room:room-ktx:${Versions.Androidx.room}")

                api("org.web3j:core:${Versions.web3j}")

                api("joda-time:joda-time:${Versions.jodaTime}")
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
