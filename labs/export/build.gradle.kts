plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
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
