plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
}

dependencies {
    // Fabric
    minecraft(group = "com.mojang", name = "minecraft", version = "${properties["minecraft_version"]}")
    mappings(group = "net.fabricmc", name = "yarn", version = "${properties["yarn_mappings"]}", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "${properties["loader_version"]}")
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "${properties["fabric_api"]}")
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

tasks.remapJar {
    archiveBaseName.set("${properties["mod_name"]}")
}