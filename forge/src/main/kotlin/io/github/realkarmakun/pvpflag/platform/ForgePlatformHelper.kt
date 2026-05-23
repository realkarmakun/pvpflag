package io.github.realkarmakun.pvpflag.platform

import io.github.realkarmakun.pvpflag.platform.services.PlatformHelper
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.FMLLoader

class ForgePlatformHelper : PlatformHelper {
    override fun getPlatformName(): String = "Forge"
    override fun isModLoaded(modId: String?): Boolean = ModList.get().isLoaded(modId)
    override fun isDevelopmentEnvironment(): Boolean = !FMLLoader.isProduction()
}
