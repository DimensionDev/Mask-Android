// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.Kotlin.lang))
        classpath("com.android.tools.build:gradle:${Versions.agp}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}


allprojects {
    configRepository()
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.Java.jvmTarget
        }
    }
}
