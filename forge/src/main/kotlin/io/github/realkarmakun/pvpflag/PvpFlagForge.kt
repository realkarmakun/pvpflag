package io.github.realkarmakun.pvpflag

import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.forge.runWhenOn
import thedarkcolour.kotlinforforge.forge.registerConfig

@Mod(Constants.MOD_ID)
object PvpFlagForge {
    init {
        registerConfig(ModConfig.Type.SERVER, PvpFlagConfig.SPEC, "pvpflag-server.toml")
        registerConfig(ModConfig.Type.CLIENT, PvpFlagConfig.CLIENT_SPEC, "pvpflag-client.toml")
        PvpFlagNetwork.registerServer()

        MinecraftForge.EVENT_BUS.addListener { event: TickEvent.ServerTickEvent ->
            if (event.phase == TickEvent.Phase.END) {
                val server = ServerLifecycleHooks.getCurrentServer() ?: return@addListener
                val overworld = server.overworld() ?: return@addListener
                PvpFlagState.get(overworld).tickServer(server)
            }
        }

        runWhenOn(Dist.CLIENT) {
            ClientSetup.setup()
        }
    }
}
