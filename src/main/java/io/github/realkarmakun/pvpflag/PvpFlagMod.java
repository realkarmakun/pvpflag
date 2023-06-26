package io.github.realkarmakun.pvpflag;

import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpFlagMod implements ModInitializer {
    public static final String MOD_ID = "pvpflag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        PvpFlagNetworkHandler.registerServerToClientPackets();

    }
}
