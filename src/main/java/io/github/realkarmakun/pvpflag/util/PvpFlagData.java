package io.github.realkarmakun.pvpflag.util;

import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class PvpFlagData {
    public static boolean hasStatus(PersistentEntityData entity) {
        final var tagData = entity.getPersistentData();
        return tagData.contains("is-up");
    }

    public static boolean getStatus(PersistentEntityData player) {
        final var tagData = player.getPersistentData();
        return tagData.getBoolean("is-up");
    }

    public static void switchStatus(PersistentEntityData player) {
        final var tagData = player.getPersistentData();
        final var newState = !tagData.getBoolean("is-up");
        tagData.putBoolean("is-up", newState);
        // Sync the Data
        syncSwitchStatus(newState, (ServerPlayer) player);
    }

    public static void setStatus(boolean state, PersistentEntityData player) {
        final var tagData = player.getPersistentData();
        tagData.putBoolean("is-up", state);
        syncSwitchStatus(state, (ServerPlayer) player);
    }

    public static void syncSwitchStatus(boolean state, ServerPlayer player) {
        var buffer = PacketByteBufs.create();
        buffer.writeBoolean(state);
        ServerPlayNetworking.send(player, PvpFlagNetworkHandler.PVP_FLAG_SYNC_ID, buffer);
    }
}
