package io.github.realkarmakun.pvpflag;

import io.github.realkarmakun.pvpflag.keybinds.PvpFlagInputHandler;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.api.ClientModInitializer;

public class PvpFlagClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        PvpFlagInputHandler.register();

        PvpFlagNetworkHandler.registerClientToServerPackets();
    }
}
