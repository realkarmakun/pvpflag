import org.gradle.kotlin.dsl.maven

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "pvpflag"

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            name = "Fuzs Mod Resources"
            url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        }
    }
    versionCatalogs {
        register("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}
include("common")
include("fabric")
include("forge")