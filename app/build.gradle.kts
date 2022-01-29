import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
}

android {
    setup()
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    val file = rootProject.file("signing.properties")
    val hasSigningProps = file.exists()

    signingConfigs {
        if (hasSigningProps) {
            create("maskbook") {
                val signingProp = Properties()
                signingProp.load(file.inputStream())
                storeFile = rootProject.file(signingProp.getProperty("storeFile"))
                storePassword = signingProp.getProperty("storePassword")
                keyAlias = signingProp.getProperty("keyAlias")
                keyPassword = signingProp.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            if (hasSigningProps) {
                signingConfig = signingConfigs.getByName("maskbook")
            }
        }
        release {
            if (hasSigningProps) {
                signingConfig = signingConfigs.getByName("maskbook")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("androidx.annotation:annotation:1.3.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.github.romandanylyk:PageIndicatorView:v.1.0.3")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation(projects.wallet)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("io.github.dimensiondev:maskwalletcore:0.4.0")
    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha04")
    implementation("org.web3j:core:4.8.8-android")
    implementation("androidx.paging:paging-runtime-ktx:3.1.0")

    implementation(projects.debankapi)

    implementation("org.mozilla.geckoview:geckoview:95.+")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("me.dm7.barcodescanner:zxing:1.9.8")
    implementation("com.github.tbruyelle:rxpermissions:0.10.1")
    implementation("com.jakewharton.rxbinding2:rxbinding:2.1.1")
}
