plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
    id("com.google.devtools.ksp").version(Versions.ksp)
    id("org.jetbrains.compose").version(Versions.compose_jb)
}

kotlin {
    android()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.routeProcessor.annotations)
                kspAndroid(projects.common.routeProcessor)
            }
        }
        val androidMain by getting {
            dependencies {

                implementation("androidx.navigation:navigation-ui-ktx:${Versions.navigation}")
                implementation("androidx.navigation:navigation-compose:${Versions.navigation}")

                implementation("joda-time:joda-time:${Versions.jodaTime}")
                implementation("io.github.dimensiondev:maskwalletcore:${Versions.maskWalletCore}")

                implementation(projects.debankapi)
                implementation(projects.common)
                implementation(projects.common.retrofit)
                implementation(projects.common.okhttp)

                implementation("androidx.compose.runtime:runtime-livedata:${Versions.Androidx.livedata}")
                api("androidx.room:room-runtime:${Versions.Androidx.room}")
                api("androidx.room:room-ktx:${Versions.Androidx.room}")
                kspAndroid("androidx.room:room-compiler:${Versions.Androidx.room}")
                implementation("androidx.room:room-paging:${Versions.Androidx.room}")
                implementation("androidx.core:core-ktx:${Versions.Androidx.core}")
                implementation("androidx.appcompat:appcompat:${Versions.Androidx.appcompat}")
                implementation("androidx.paging:paging-runtime-ktx:${Versions.Androidx.paging}")
                implementation("androidx.paging:paging-compose:${Versions.Androidx.pagingCompose}")
                implementation("com.journeyapps:zxing-android-embedded:${Versions.zxing}")
                implementation("com.google.android.material:material:${Versions.material}")
                implementation("com.github.WalletConnect:kotlin-walletconnect-lib:${Versions.walletConnectV1}")
                implementation("com.squareup.moshi:moshi:${Versions.moshi}")
                implementation("com.github.komputing.khex:extensions:${Versions.khexExtension}")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.test.ext:junit:1.1.3")
                implementation("androidx.test.espresso:espresso-core:3.4.0")
            }
        }
    }
}

android {
    setupLibrary()
}
