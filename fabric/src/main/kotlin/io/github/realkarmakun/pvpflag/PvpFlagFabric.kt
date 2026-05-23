package io.github.realkarmakun.pvpflag

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.neoforged.fml.config.ModConfig

fun init() {
    NeoForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.SERVER, PvpFlagConfig.SPEC, "pvpflag-server.toml")
    NeoForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, PvpFlagConfig.CLIENT_SPEC, "pvpflag-client.toml")
    PvpFlagNetwork.common()

    ServerTickEvents.END_SERVER_TICK.register { server ->
        val overworld = server.overworld() ?: return@register
        PvpFlagState.get(overworld).tickServer(server)
    }
}
