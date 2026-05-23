package io.github.realkarmakun.pvpflag

import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT


@Mod(Constants.MOD_ID)
@EventBusSubscriber(modid = Constants.MOD_ID)
object PvpFlagNeoForge {
    init {
        LOADING_CONTEXT.activeContainer.registerConfig(ModConfig.Type.SERVER, PvpFlagConfig.SPEC, "pvpflag-server.toml")
        LOADING_CONTEXT.activeContainer.registerConfig(ModConfig.Type.CLIENT, PvpFlagConfig.CLIENT_SPEC, "pvpflag-client.toml")
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        PvpFlagNetwork.common()
    }

    @SubscribeEvent
    fun onServerTickEvent(event: ServerTickEvent.Post) {
        val server = ServerLifecycleHooks.getCurrentServer() ?: return
        val overworld = server.overworld() ?: return
        PvpFlagState.get(overworld).tickServer(server)
    }
}
