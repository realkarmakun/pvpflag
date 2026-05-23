package io.github.realkarmakun.pvpflag.platform

import io.github.realkarmakun.pvpflag.platform.services.PlatformHelper
import net.fabricmc.loader.api.FabricLoader

class FabricPlatformHelper : PlatformHelper {
    override fun getPlatformName(): String = "Fabric"
    override fun isModLoaded(modId: String?): Boolean = FabricLoader.getInstance().isModLoaded(modId)
    override fun isDevelopmentEnvironment(): Boolean = FabricLoader.getInstance().isDevelopmentEnvironment
}
