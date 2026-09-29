pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("fabric-loom") version providers.gradleProperty("loom_version").get()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
}

// One codebase for every Minecraft version (ADR 0009). vcsVersion is what the
// committed sources are written for; keep `stonecutter active` equal to it.
stonecutter {
    create(rootProject) {
        versions("1.20.1", "1.21.1")
        vcsVersion = "1.21.1"
    }
}

rootProject.name = "flattersigns"
