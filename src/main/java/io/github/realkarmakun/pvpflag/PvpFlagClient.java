package io.github.realkarmakun.pvpflag;

import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay;
import io.github.realkarmakun.pvpflag.keybinds.PvpFlagInputHandler;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class PvpFlagClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        PvpFlagInputHandler.register();

        PvpFlagNetworkHandler.registerServerToClientPackets();

        HudRenderCallback.EVENT.register(new PvpFlagHudOverlay());
    }
}
