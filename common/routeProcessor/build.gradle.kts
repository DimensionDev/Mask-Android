plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:${Versions.ksp}")
    implementation("com.squareup:kotlinpoet:${Versions.kotlinpoet}")
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinpoet}")
}
