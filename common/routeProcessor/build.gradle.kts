plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.common.routeProcessor.annotations)
    implementation("com.google.devtools.ksp:symbol-processing-api:${Versions.ksp}")
    implementation("com.squareup:kotlinpoet:${Versions.kotlinpoet}")
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinpoet}")
}
