import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
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
    implementation("androidx.core:core-splashscreen:1.0.0-beta02")
    implementation(projects.entry)
    implementation(projects.common)
    implementation(projects.common.gecko)

    // Koin
    implementation("io.insert-koin:koin-android:${Versions.koin}")

    if (enableFirebase) {
        implementation("com.google.firebase:firebase-analytics-ktx:${Versions.Firebase.analytics}")
        implementation(platform("com.google.firebase:firebase-bom:${Versions.Firebase.bom}"))
        implementation("com.google.firebase:firebase-crashlytics-ktx:${Versions.Firebase.crashlytics}")
    }
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
