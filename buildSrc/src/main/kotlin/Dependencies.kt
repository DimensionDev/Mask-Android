import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

fun Project.configRepository() {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.mozilla.org/maven2/")
    }
}