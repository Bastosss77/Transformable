plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.6.10-1.0.2"
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.0"))
    }
}

dependencies {
    implementation(project(":Transformable-Processor"))
    ksp(project(":Transformable-Processor"))
}