import org.gradle.api.JavaVersion

object Versions {
    object Android {
        const val min = 21
        const val compile = 31
        const val target = compile
        const val buildTools = "31.0.0"
    }

    object Kotlin {
        const val lang = "1.6.10"
        const val coroutines = "1.6.0"
        const val serialization = "1.3.2"
    }

    object Java {
        const val jvmTarget = "11"
        val java = JavaVersion.VERSION_11
    }

    const val ksp = "${Kotlin.lang}-1.0.2"
    const val agp = "7.1.0"
    const val spotless = "6.2.0"
    const val ktlint = "0.43.2"
    const val compose_jb = "1.1.0-alpha02"
    const val compose = "1.1.0-rc03"
    const val accompanist = "0.22.1-rc"
    const val navigation = "2.4.0"
    const val lifecycle = "2.4.0"
    const val koin = "3.1.4"
    const val room = "2.4.1"
    const val coil = "1.4.0"
    const val datastore = "1.0.0"
    const val biometric = "1.2.0-alpha04"
    const val retrofit = "2.9.0"
    const val okhttp = "4.9.2"
    const val web3j = "4.8.8-android"
}
