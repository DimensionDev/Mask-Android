plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Kotlin.serialization}")
                implementation("com.ensarsarajcic.kotlinx:serialization-msgpack:${Versions.kotlinxSerializationMsgPackVersion}")
            }
        }
        val commonTest by getting {
            dependencies {
            }
        }
    }
}

java {
    sourceCompatibility = Versions.Java.java
    targetCompatibility = Versions.Java.java
}
