pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.mozilla.org/maven2/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "Mask"

include(
    ":app",
    ":common",
    ":common:okhttp",
    ":common:retrofit",
    ":common:routeProcessor",
    ":localization",
    ":wallet",
    ":wallet:export",
    ":debankapi",
    ":labs",
    ":labs:export",
    ":persona",
    ":persona:export",
    ":setting",
    ":setting:export",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
