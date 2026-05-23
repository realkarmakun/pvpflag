package io.github.realkarmakun.pvpflag.platform

import io.github.realkarmakun.pvpflag.platform.services.PlatformHelper
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import kotlin.text.get

class NeoForgePlatformHelper : PlatformHelper {
    override fun getPlatformName(): String {
        return "NeoForge"
    }

    override fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
    }

    override fun isDevelopmentEnvironment(): Boolean {
        return !FMLLoader.isProduction()
    }
}
