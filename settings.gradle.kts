pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "Mask"
include(":app", ":wallet", ":debankapi")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")