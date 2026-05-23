plugins {
    // see https://fabricmc.net/develop/ for new versions
    alias(libs.plugins.loom) apply false
    // see https://projects.neoforged.net/neoforged/moddevgradle for new versions
    alias(libs.plugins.moddev) apply false
    alias(libs.plugins.minotaur) apply false
}

allprojects {
    repositories {
        maven {
            url = uri("https://maven2.bai.lol")
            content {
                includeGroup("lol.bai")
            }
        }
        maven {
            name = "Fuzs Mod Resources"
            url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        }
    }
}