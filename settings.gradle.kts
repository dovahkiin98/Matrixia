@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        maven { url = uri("https://maven.google.com") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.google.com") }
        google()
        mavenCentral()
    }
}
rootProject.name = "Matrixia"
include(":app")

enableFeaturePreview("VERSION_CATALOGS")