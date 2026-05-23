package io.github.realkarmakun.pvpflag

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraftforge.fml.config.ModConfig

fun init() {
    ForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.SERVER, PvpFlagConfig.SPEC, "pvpflag-server.toml")
    ForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, PvpFlagConfig.CLIENT_SPEC, "pvpflag-client.toml")
    PvpFlagNetwork.registerServer()

    ServerTickEvents.END_SERVER_TICK.register { server ->
        val overworld = server.overworld() ?: return@register
        PvpFlagState.get(overworld).tickServer(server)
    }
}
