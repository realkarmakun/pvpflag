package io.github.realkarmakun.pvpflag;

import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay;
import io.github.realkarmakun.pvpflag.keybinds.PvpFlagInputHandler;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class PvpFlagClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        PvpFlagInputHandler.register();

        HudRenderCallback.EVENT.register(new PvpFlagHudOverlay());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                ClientPlayNetworking.send(PvpFlagNetworkHandler.PVP_FLAG_SYNC_REQ_ID, PacketByteBufs.create()));
    }
}
