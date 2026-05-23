package io.github.realkarmakun.pvpflag

import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay
import io.github.realkarmakun.pvpflag.keybinds.PvpFlagKeybinds
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent

@EventBusSubscriber(modid = Constants.MOD_ID, value = [Dist.CLIENT])
object PvpFlagNeoForgeClient {

    @SubscribeEvent
    private fun onClientSetup(event: FMLClientSetupEvent) {
        PvpFlagNetwork.client()
    }

    @SubscribeEvent
    private fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        event.register(PvpFlagKeybinds.createKeyMapping())
    }

    @SubscribeEvent
    private fun onRegisterOverlays(event: RegisterGuiLayersEvent) {
        event.registerAboveAll("skull-state".location()) { graphics, deltaTracker ->
            PvpFlagHudOverlay.render(graphics, deltaTracker.getGameTimeDeltaPartialTick(false))
        }
    }

    @SubscribeEvent
    private fun onClientTick(event: ClientTickEvent.Post) {
        PvpFlagClientState.tick()
        val client = Minecraft.getInstance()
        if (client.player != null) {
            PvpFlagKeybinds.onClientTick { PvpFlagNetwork.sendSwitchPacket() }
        }
    }
}
