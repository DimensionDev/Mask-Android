import org.gradle.api.JavaVersion

object Versions {
    object Kotlin {
        const val lang = "1.6.10"
    }

    object Java {
        const val jvmTarget = "11"
        val java = JavaVersion.VERSION_11
    }

    const val agp = "7.0.4"
    const val ksp = "${Kotlin.lang}-1.0.2"
    const val spotless = "6.2.0"
    const val ktlint = "0.43.2"
}
