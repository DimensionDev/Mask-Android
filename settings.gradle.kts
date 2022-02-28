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
    ":common:gecko",
    ":common:gecko:sample",
    ":common:okhttp",
    ":common:retrofit",
    ":common:routeProcessor",
    ":common:routeProcessor:annotations",
    ":common:bigDecimal",
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
    ":extension",
    ":extension:export",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
