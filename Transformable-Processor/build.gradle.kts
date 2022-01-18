plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup:kotlinpoet-ksp:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.4.1")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.10-1.0.2")
}