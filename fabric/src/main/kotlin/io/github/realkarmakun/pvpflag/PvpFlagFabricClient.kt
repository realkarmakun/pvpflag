package io.github.realkarmakun.pvpflag

import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay
import io.github.realkarmakun.pvpflag.keybinds.PvpFlagKeybinds
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

fun initClient() {
    PvpFlagNetwork.registerClient()

    KeyBindingHelper.registerKeyBinding(PvpFlagKeybinds.createKeyMapping())
    ClientTickEvents.END_CLIENT_TICK.register { client ->
        PvpFlagClientState.tick()
        if (client.player != null) {
            PvpFlagKeybinds.onClientTick { PvpFlagNetwork.sendSwitchPacket() }
        }
    }

    HudRenderCallback.EVENT.register { graphics, tickDelta ->
        PvpFlagHudOverlay.render(graphics, tickDelta)
    }
}
