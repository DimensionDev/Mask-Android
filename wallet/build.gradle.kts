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
        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.4.0")
                implementation("androidx.compose.runtime:runtime-livedata:1.0.5")
                // implementation("com.google.android.material:material:1.6.0-alpha02")

                implementation("androidx.navigation:navigation-ui-ktx:${Versions.navigation}")
                implementation("androidx.navigation:navigation-compose:${Versions.navigation}")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
                implementation("com.squareup.retrofit2:retrofit:2.9.0")
                implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
                implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
                implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")
                implementation("com.squareup.okhttp3:okhttp:4.9.2")
                implementation("joda-time:joda-time:2.10.13")
                implementation(("org.web3j:core:4.8.8-android"))
                implementation("io.github.dimensiondev:maskwalletcore:0.4.0")

                implementation(projects.debankapi)
                api(projects.common)

                api("androidx.room:room-runtime:${Versions.room}")
                api("androidx.room:room-ktx:${Versions.room}")
                project.dependencies.add("kspAndroid", "androidx.room:room-compiler:${Versions.room}")
                implementation("androidx.room:room-paging:${Versions.room}")

                implementation("androidx.paging:paging-runtime-ktx:3.1.0")
                implementation("androidx.paging:paging-compose:1.0.0-alpha14")

                implementation("com.journeyapps:zxing-android-embedded:4.3.0")

                implementation("androidx.core:core-ktx:1.7.0")
                implementation("androidx.appcompat:appcompat:1.4.1")
                implementation("com.google.android.material:material:1.5.0")

                implementation("androidx.biometric:biometric-ktx:1.2.0-alpha04")
                implementation("com.github.WalletConnect:kotlin-walletconnect-lib:0.9.7")
                implementation("com.squareup.moshi:moshi:1.8.0")
                implementation("com.github.komputing.khex:extensions:1.1.2")
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
