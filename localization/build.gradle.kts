import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
}

android {
    setupLibrary()
}

tasks.create("generateTranslation") {
    doLast {
        val localizationFolder = File(rootDir, "localization/crowdin")
        localizationFolder.listFiles()?.forEach { file ->
            val name = file.name.substringAfter("app").trimStart('-').substringBefore(".json")
            val target = if (name.isEmpty()) {
                File(projectDir, "src/androidMain/res/values/strings.xml")
            } else {
                File(
                    projectDir,
                    "src/androidMain/res/values-${name.split("_").first()}-r${name.split("_").last()}/strings.xml"
                )
            }
            generateLocalization(file, target)
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
