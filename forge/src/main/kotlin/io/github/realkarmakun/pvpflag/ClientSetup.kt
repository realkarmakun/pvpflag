package io.github.realkarmakun.pvpflag

import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay
import io.github.realkarmakun.pvpflag.keybinds.PvpFlagKeybinds
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object ClientSetup {
    fun setup() {
        MOD_BUS.addListener(::onClientSetup)
        MOD_BUS.addListener(::onRegisterKeyMappings)
        MOD_BUS.addListener(::onRegisterOverlays)
        MinecraftForge.EVENT_BUS.register(ClientTickHandler)
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        PvpFlagNetwork.registerClient()
    }

    private fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        event.register(PvpFlagKeybinds.createKeyMapping())
    }

    private fun onRegisterOverlays(event: RegisterGuiOverlaysEvent) {
        event.registerAboveAll("pvpflag_hud") { _, poseStack, partialTick, _, _ ->
            PvpFlagHudOverlay.render(poseStack, partialTick)
        }
    }

    private object ClientTickHandler {
        @SubscribeEvent
        fun onClientTick(event: TickEvent.ClientTickEvent) {
            if (event.phase == TickEvent.Phase.END) {
                PvpFlagClientState.tick()
                val client = Minecraft.getInstance()
                if (client.player != null) {
                    PvpFlagKeybinds.onClientTick { PvpFlagNetwork.sendSwitchPacket() }
                }
            }
        }
    }
}
