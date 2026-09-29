plugins {
    id("fabric-loom")
}

// Every Minecraft since 1.20.5 needs Java 21.
val javaVersion = if (sc.current.parsed >= "1.20.5") 21 else 17

version = "${property("mod_version")}+${sc.current.version}"
group = property("maven_group") as String

base {
    archivesName = property("archives_base_name") as String
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("flattersigns") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["client"])
        }
    }

    mixin {
        useLegacyMixinAp = true
        defaultRefmapName = "flattersigns.refmap.json"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
}

tasks.withType<ProcessResources>().configureEach {
    val props = mapOf(
        "version" to project.version.toString(),
        "minecraft" to project.property("mc_compat") as String,
    )
    inputs.properties(props)
    inputs.property("java", javaVersion)

    filesMatching("fabric.mod.json") { expand(props) }
    filesMatching("*.mixins.json") { expand("java" to javaVersion) }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = javaVersion
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks.jar {
    val archivesName = base.archivesName
    inputs.property("archivesName", archivesName)

    from(rootProject.file("LICENSE")) {
        rename { "${it}_${archivesName.get()}" }
    }
}
