
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

fun com.android.build.gradle.internal.dsl.BaseAppModuleExtension.setup() {
    compileSdk = Versions.Android.compile
    buildToolsVersion = Versions.Android.buildTools
    defaultConfig {
        applicationId = Package.id
        minSdk = Versions.Android.min
        targetSdk = Versions.Android.target
        versionCode = Package.versionCode
        versionName = Package.versionName
    }

    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
    }
}

fun com.android.build.api.dsl.LibraryExtension.setupLibrary() {
    compileSdk = Versions.Android.compile
    defaultConfig {
        minSdk = Versions.Android.min
        targetSdk = Versions.Android.target
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["debug"].java.srcDir("build/generated/ksp/androidDebug/kotlin")
}


fun com.android.build.gradle.LibraryExtension.withCompose() {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

fun Project.kspAndroid(dependencyNotation: Any) {
    project.dependencies.add("kspAndroid", dependencyNotation)
}

val Project.enableFirebase: Boolean
    get() = file("google-services.json").exists()
