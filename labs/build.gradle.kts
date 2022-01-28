plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = Package.group
version = Package.versionName

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
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
    setup()
    withCompose()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}