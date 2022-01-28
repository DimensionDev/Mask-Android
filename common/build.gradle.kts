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
                api(projects.wallet.export)
                api(projects.labs.export)
                api(projects.persona.export)
                api(projects.setting.export)
                api("androidx.compose.ui:ui:${Versions.compose}")
                api("androidx.compose.ui:ui-util:${Versions.compose}")
                api("androidx.compose.foundation:foundation:${Versions.compose}")
                api("androidx.compose.material:material:${Versions.compose}")
                api("androidx.compose.material:material-icons-core:${Versions.compose}")
                api("androidx.compose.material:material-icons-extended:${Versions.compose}")
            }
        }
        val androidDebug by getting {
            dependencies {
                api("androidx.compose.ui:ui-tooling:${Versions.compose}")
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