import groovy.util.Node
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
    id("com.google.devtools.ksp").version(Versions.ksp)
}

android {
    setup()
    withCompose()
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.5")
    // implementation("com.google.android.material:material:1.6.0-alpha02")

    implementation("com.google.accompanist:accompanist-pager:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-swiperefresh:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-navigation-animation:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-navigation-material:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-permissions:${Versions.accompanist}")
    implementation("androidx.navigation:navigation-ui-ktx:${Versions.navigation}")
    implementation("androidx.navigation:navigation-compose:${Versions.navigation}")
    implementation("io.coil-kt:coil-compose:1.4.0")
    implementation("io.coil-kt:coil-svg:1.4.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")

    api("io.insert-koin:koin-android:${Versions.koin}")
//    implementation("io.insert-koin:koin-android-viewmodel:${Versions.koin}")
    implementation("io.insert-koin:koin-androidx-compose:${Versions.koin}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("joda-time:joda-time:2.10.13")
    implementation(("org.web3j:core:4.8.8-android"))
    implementation("io.github.dimensiondev:maskwalletcore:0.4.0")

    implementation(projects.debankapi)
    implementation(projects.common)

    api("androidx.room:room-runtime:${Versions.room}")
    api("androidx.room:room-ktx:${Versions.room}")
    ksp("androidx.room:room-compiler:${Versions.room}")
    implementation("androidx.room:room-paging:${Versions.room}")

    implementation("androidx.paging:paging-runtime-ktx:3.1.0")
    implementation("androidx.paging:paging-compose:1.0.0-alpha14")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha04")
    implementation("com.github.WalletConnect:kotlin-walletconnect-lib:0.9.7")
    implementation("com.squareup.moshi:moshi:1.8.0")
    implementation("com.github.komputing.khex:extensions:1.1.2")
}

tasks.create("generateTranslation") {
    doLast {
        val localizationFolder = File(rootDir, "localization")
        localizationFolder.listFiles()?.forEach { file ->
            val name = file.name.substringAfter("app").trimStart('-').substringBefore(".json")
            val target = if (name.isEmpty()) {
                File(projectDir, "src/main/res/values/strings.xml")
            } else {
                File(
                    projectDir,
                    "src/main/res/values-${name.split("_").first()}-r${name.split("_").last()}/strings.xml"
                )
            }.apply {
                ensureParentDirsCreated()
                if (!exists()) {
                    createNewFile()
                }
            }
            generateLocalization(file, target)
        }
    }
}

tasks.create("replaceText") {
    doLast {
        val xml = File(projectDir, "src/main/res/values/strings.xml").let {
            groovy.xml.XmlParser().parse(it).children()
        }?.let {
            it.map {
                it as Node
            }.associate {
                it.attribute("name").toString() to it.value().toString().trimStart('[').trimEnd(']')
            }
        }?.let { map ->
            File(projectDir, "src/main/java").walk().forEach { file ->
                if (file.isFile) {
                    var text = file.readText()
                    map.forEach { (key, value) ->
                        text = text.replace(
                            " \"${value}\"",
                            " androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.$key)"
                        )
                    }
                    file.writeText(text)
                }
            }
        }
    }
}

fun String.escapeXml(): String {
    return this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}

fun generateLocalization(appJson: File, target: File) {
    val json = appJson.readText(Charsets.UTF_8)
    val obj = org.json.JSONObject(json)
    val result = flattenJson(obj).filter {
        it.value.isNotEmpty() && it.value.isNotBlank()
    }
    if (result.isNotEmpty()) {
        target.apply {
            ensureParentDirsCreated()
            if (!exists()) {
                createNewFile()
            }
        }
        val xml =
            """<resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">""" + System.lineSeparator() +
                result.map {
                    "    <string name=\"${it.key}\">${
                    it.value.escapeXml().replace(System.lineSeparator(), "\\n")
                        .replace("%@", "%s")
                    }</string>"
                }.joinToString(System.lineSeparator()) + System.lineSeparator() +
                "</resources>"
        target.writeText(xml)
    }
}

fun flattenJson(obj: org.json.JSONObject): Map<String, String> {
    return obj.toMap().toList().flatMap { it ->
        val (key, value) = it
        when (value) {
            is org.json.JSONObject -> {
                flattenJson(value).map {
                    "${key}_${it.key}" to it.value
                }.toList()
            }
            is Map<*, *> -> {
                flattenJson(org.json.JSONObject(value)).map {
                    "${key}_${it.key}" to it.value
                }.toList()
            }
            is String -> {
                listOf(key to value)
            }
            else -> {
                listOf(key to value.toString())
            }
        }
    }.toMap()
}
