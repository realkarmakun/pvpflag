import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("multiloader-loader")
    alias(libs.plugins.moddev)
    alias(libs.plugins.minotaur)
}

val modId: String by project
val modrinthProjectId: String by project
val modrinthVersionType: String by project

mixin {
    add(sourceSets.main.get(), "${modId}.refmap.json")
    config("${modId}.mixins.json")
    config("${modId}.forge.mixins.json")
}
tasks.jar {
    manifest {
        attributes["MixinConfigs"] = "${modId}.mixins.json,${modId}.forge.mixins.json"
    }
}

legacyForge {
    version = libs.versions.forge.get()
    // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = libs.versions.parchmentMC
        mappingsVersion = libs.versions.parchment
    }
    runs {
        configureEach {
            systemProperty("forge.enabledGameTestNamespaces", modId)
            ideName = "Forge ${name.capitalized()} (${project.path})" // Unify the run config names with fabric
        }
        register("client") {
            client()
        }
        register("data") {
            data()
        }
        register("server") {
            server()
        }
    }
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

dependencies {
    implementation(libs.kff)
    modImplementation("lol.bai:badpackets:forge-${libs.versions.badpackets.get()}")
    annotationProcessor(variantOf(libs.mixin) { classifier("processor") })
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN") ?: "DEBUG_TOKEN")
    projectId = modrinthProjectId
    versionNumber.set("${project.version}")
    versionType.set(modrinthVersionType)
    uploadFile.set(tasks.jar)
    gameVersions.addAll("1.20.1")
    loaders.addAll("forge","neoforge")
    debugMode.set(System.getenv("MODRINTH_DEBUG")?.toBoolean() ?: true)
    syncBodyFrom = rootProject.file("README.md").readText()
    changelog = rootProject.file("CHANGELOG.md").readText()
    dependencies {
        required.project("badpackets")
        required.project("kotlin-for-forge")
    }
}