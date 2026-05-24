import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("multiloader-loader")
    alias(libs.plugins.moddev)
    alias(libs.plugins.minotaur)
}

val modId: String by project
val modrinthProjectId: String by project
val modrinthVersionType: String by project

neoForge {
    version = libs.versions.neoforge.get()
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
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            ideName = "NeoForge ${name.capitalized()} (${project.path})" // Unify the run config names with fabric
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
    // Badpackets is putting its platform in version instead of making a separate artifact (very naughty)
    // So we do this bullshit, God bless Kotlin
    implementation("lol.bai:badpackets:neo-${libs.versions.badpackets.get()}")
    // annotationProcessor(variantOf(libs.mixin) { classifier("processor") })
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN") ?: "DEBUG_TOKEN")
    projectId = modrinthProjectId
    versionNumber.set("${project.version}")
    versionType.set(modrinthVersionType)
    uploadFile.set(tasks.jar)
    gameVersions.addAll(libs.versions.minecraft.get())
    loaders.addAll("neoforge")
    debugMode.set(System.getenv("MODRINTH_DEBUG")?.toBoolean() ?: true)
    syncBodyFrom = rootProject.file("README.md").readText()
    changelog = rootProject.file("CHANGELOG.md").readText()
    dependencies {
        required.project("badpackets")
        required.project("kotlin-for-forge")
    }
}