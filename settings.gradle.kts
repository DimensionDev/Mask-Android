pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "Mask"

include(
    ":app",
    ":wallet",
    ":wallet:export",
    ":debankapi",
    ":common",
    ":labs",
    ":labs:export",
    ":persona",
    ":persona:export",
    ":setting",
    ":setting:export",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
