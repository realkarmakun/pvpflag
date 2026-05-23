plugins {
    id("multiloader-loader")
    alias(libs.plugins.loom)
    alias(libs.plugins.minotaur)
}

val modId: String by project
val modrinthProjectId: String by project
val modrinthVersionType: String by project

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchmentMC.get()}:${libs.versions.parchment.get()}@zip")
    })
    modImplementation(libs.fabricLoader)
    modImplementation(libs.fabricApi)

    modImplementation(libs.flk)
    // Badpackets is putting its platform in version instead of making a separate artifact (very naughty)
    // So we do this bullshit, God bless Kotlin
    modImplementation("lol.bai:badpackets:fabric-${libs.versions.badpackets.get()}")

    modApi(libs.fcap.fabric)

    modLocalRuntime("com.terraformersmc:modmenu:11.0.4")
}

loom {
    val aw = project(":common").file("src/main/resources/${modId}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }
    runs {
        named("client") {
            client()
            setConfigName("Fabric Client")
            ideConfigGenerated(true)
            runDir("runs/client")
        }
        named("server") {
            server()
            setConfigName("Fabric Server")
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN") ?: "DEBUG_TOKEN")
    projectId.set(modrinthProjectId)
    versionNumber.set("${project.version}")
    versionType.set(modrinthVersionType)
    uploadFile.set(tasks.remapJar)
    gameVersions.addAll("1.21.1")
    loaders.add("fabric")
    debugMode.set(System.getenv("MODRINTH_DEBUG")?.toBoolean() ?: true)
    syncBodyFrom = rootProject.file("README.md").readText()
    changelog = rootProject.file("CHANGELOG.md").readText()
    dependencies {
        required.project("fabric-api")
        required.project("fabric-language-kotlin")
        required.project("badpackets")
        required.project("forge-config-api-port")
    }
}