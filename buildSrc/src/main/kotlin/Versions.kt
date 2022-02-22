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
    const val coil = "1.4.0"
    const val datastore = "1.0.0"
    const val retrofit = "2.9.0"
    const val retrofitSerialization = "0.8.0"
    const val okhttp = "4.9.2"
    const val web3j = "4.8.8-android"
    const val kotlinpoet = "1.10.2"
    const val material = "1.5.0"
    const val khexExtension = "1.1.2"
    const val moshi = "1.8.0"
    const val walletConnectV1 = "0.9.7"
    const val zxing = "4.3.0"
    const val maskWalletCore = "0.4.0"
    const val jodaTime = "2.10.13"
    const val gecko = "97.+"
    object Firebase {
        object Plugin {
            const val crashlytics = "2.8.1"
            const val google_services = "4.3.10"
        }
        const val analytics = "20.1.0"
        const val crashlytics = "18.2.8"
        const val bom = "29.1.0"
    }

    object Androidx{
        const val core = "1.7.0"
        const val appcompat = "1.4.1"
        const val room = "2.4.1"
        const val paging = "3.1.0"
        const val pagingCompose = "1.0.0-alpha14"
        const val livedata = "1.0.5"
        const val annotation = "1.3.0"
        const val activityCompose = "1.4.0"
        const val constraintlayout = "2.1.2"
        const val preference = "1.1.1"
        const val biometric = "1.2.0-alpha04"
    }
}
