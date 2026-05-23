package io.github.realkarmakun.pvpflag.platform.services

interface PlatformHelper {
    fun getPlatformName(): String
    fun isModLoaded(modId: String?): Boolean
    fun isDevelopmentEnvironment(): Boolean
    fun getEnvironmentName(): String {
        return if (isDevelopmentEnvironment()) "development" else "production"
    }
}
