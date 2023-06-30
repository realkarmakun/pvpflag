package io.github.realkarmakun.pvpflag;

import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay;
import io.github.realkarmakun.pvpflag.keybinds.PvpFlagInputHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class PvpFlagClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        PvpFlagInputHandler.register();

        HudRenderCallback.EVENT.register(new PvpFlagHudOverlay());

    }
}
