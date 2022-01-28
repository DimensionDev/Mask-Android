plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.1.0")
    api(kotlin("gradle-plugin", version = "1.6.10"))
}