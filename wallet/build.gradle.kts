plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose").version(Versions.compose_jb)
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
    id("com.google.devtools.ksp").version(Versions.ksp)
}

kotlin {
    android()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.routeProcessor.annotations)
                kspAndroid(projects.common.routeProcessor)
            }
        }
        val commonTest by getting {
            dependencies {
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(projects.common)
                implementation(projects.debankapi)

                kspAndroid("androidx.room:room-compiler:${Versions.Androidx.room}")

                implementation("androidx.room:room-paging:${Versions.Androidx.room}")
                implementation("androidx.paging:paging-runtime-ktx:${Versions.Androidx.paging}")
                implementation("androidx.paging:paging-compose:${Versions.Androidx.pagingCompose}")

                implementation("io.github.dimensiondev:maskwalletcore:${Versions.maskWalletCore}")
                implementation("com.github.WalletConnect:kotlin-walletconnect-lib:${Versions.walletConnectV1}")
                implementation("com.squareup.moshi:moshi:${Versions.moshi}")
                implementation("com.github.komputing.khex:extensions:${Versions.khexExtension}")
            }
        }
        val androidTest by getting {
            dependencies {
            }
        }
    }
}

android {
    setupLibrary()
}

fun findAndroidManifest(file: File): File? {
    if (file.name == "AndroidManifest.xml") {
        return file
    }
    if (file.isDirectory) {
        val list = file.listFiles()
        if (list.isNullOrEmpty()) return null
        for (child in list) {
            val result = findAndroidManifest(child)
            if (result != null) {
                return result
            }
        }
    }
    return null
}
val addQueriesForWalletConnect by tasks.registering {
    doLast {
        val generatedDir = File(project.buildDir, "intermediates/merged_manifest/")
        val json = File(project.projectDir, "src/androidMain/assets/wallet_connect.json").readText(Charsets.UTF_8)
        val jsonObj = org.json.JSONObject(json)
        val packages = mutableListOf<String>()
        jsonObj.keys().let {
            while (it.hasNext()) {
                val obj = jsonObj.get(it.next())
                if (obj is org.json.JSONObject) {
                    obj.getJSONObject("app").getString("android")?.let { url ->
                        url.split("?").last().split("&").forEach { query ->
                            if (query.startsWith("id=")) {
                                packages.add(query.substring(3))
                            }
                        }
                    }
                }
            }
        }
        if (generatedDir.exists()) {
            generatedDir.listFiles()?.forEach {
                findAndroidManifest(it)?.let { file ->
                    val manifest = groovy.xml.XmlParser().parse(file)
                    val queries = (manifest["queries"] as groovy.util.NodeList)[0] as groovy.util.Node
                    packages.forEach {
                        queries.appendNode("package", mapOf("android:name" to it, "xmlns:android" to "http://schemas.android.com/apk/res/android"))
                    }
                    file.writeText(groovy.xml.XmlUtil.serialize(manifest))
                }
            }
        }
    }
}

afterEvaluate {
    tasks.getByName("processDebugManifest").finalizedBy(addQueriesForWalletConnect)
    tasks.getByName("processReleaseManifest").finalizedBy(addQueriesForWalletConnect)
}
