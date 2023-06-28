package io.github.realkarmakun.pvpflag;

import io.github.realkarmakun.pvpflag.events.OnPlayerHurtEvent;
import io.github.realkarmakun.pvpflag.events.PlayerHurtEventCallback;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import io.github.realkarmakun.pvpflag.util.PvpFlagData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpFlagMod implements ModInitializer {
    public static final String MOD_ID = "pvpflag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        PvpFlagNetworkHandler.registerClientToServerPackets();

        PlayerHurtEventCallback.EVENT.register(new OnPlayerHurtEvent());

        // Set PvP flag as false on first join.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.getPlayer() instanceof PersistentEntityData player && !PvpFlagData.hasStatus(player)) {
                PvpFlagData.setStatus(false, player);
            }
        });

    }
}
