rootProject.name = "Tranformable-Project"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.6.0"
    }

    repositories {
        gradlePluginPortal()
    }
}

include("Transformable-Processor")
