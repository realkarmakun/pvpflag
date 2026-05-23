package io.github.realkarmakun.pvpflag.platform

import io.github.realkarmakun.pvpflag.Constants
import io.github.realkarmakun.pvpflag.platform.services.PlatformHelper
import java.util.ServiceLoader

object Services {
    @JvmField
    val PLATFORM: PlatformHelper = load(PlatformHelper::class.java)

    fun <T> load(clazz: Class<T>): T {
        val loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow {
                IllegalStateException("Failed to load service for ${clazz.name}")
            }
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz)
        return loadedService
    }
}
