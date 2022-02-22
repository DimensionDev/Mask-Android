import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
    id("org.jetbrains.compose").version(Versions.compose_jb)
    id("com.google.gms.google-services").version(Versions.Firebase.Plugin.google_services).apply(false)
    id("com.google.firebase.crashlytics").version(Versions.Firebase.Plugin.crashlytics).apply(false)
}

if (enableFirebase) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
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
    implementation("androidx.activity:activity-compose:${Versions.Androidx.activityCompose}")
    implementation("androidx.annotation:annotation:${Versions.Androidx.annotation}")
    implementation("androidx.appcompat:appcompat:${Versions.Androidx.appcompat}")
    implementation("androidx.core:core-ktx:${Versions.Androidx.core}")
    implementation("androidx.constraintlayout:constraintlayout:${Versions.Androidx.constraintlayout}")
    implementation("androidx.preference:preference-ktx:${Versions.Androidx.preference}")
    implementation("androidx.biometric:biometric-ktx:${Versions.Androidx.biometric}")
    implementation("com.google.android.material:material:${Versions.material}")
    implementation(projects.wallet)
    implementation(projects.persona)
    implementation(projects.labs)
    implementation(projects.setting)
    implementation("io.github.dimensiondev:maskwalletcore:${Versions.maskWalletCore}")

    if (enableFirebase) {
        implementation("com.google.firebase:firebase-analytics-ktx:${Versions.Firebase.analytics}")
        implementation(platform("com.google.firebase:firebase-bom:${Versions.Firebase.bom}"))
        implementation("com.google.firebase:firebase-crashlytics-ktx:${Versions.Firebase.crashlytics}")
    }

    implementation("org.web3j:core:${Versions.web3j}")
    implementation("androidx.paging:paging-runtime-ktx:${Versions.Androidx.paging}")

    implementation(projects.debankapi)

    implementation(projects.common)
    implementation(projects.common.okhttp)

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.mozilla.geckoview:geckoview:95.+")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("me.dm7.barcodescanner:zxing:1.9.8")
    implementation("com.github.tbruyelle:rxpermissions:0.10.1")
    implementation("com.jakewharton.rxbinding2:rxbinding:2.1.1")
}
