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
        maven("https://maven.mozilla.org/maven2/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

rootProject.name = "Mask"

include(
    ":app",
    ":common",
    ":common:gecko",
    ":common:gecko:sample",
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
    ":entry",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
