package io.github.realkarmakun.pvpflag.networking.packets;

import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class PvpFlagSyncServerToClient {

    public static void receive(
            Minecraft client,
            ClientPacketListener handler,
            FriendlyByteBuf buffer,
            PacketSender responseSender) {
        final var recivedUUID = buffer.readUUID();
        if (client.player instanceof PersistentEntityData player && client.player.getUUID().equals(recivedUUID)) {
            player.getPersistentData().putBoolean("is-up", buffer.readBoolean());
        } else if (client.level != null) {
            if (client.level.getPlayerByUUID(recivedUUID) instanceof PersistentEntityData otherPlayerData) {
                otherPlayerData.getPersistentData().putBoolean("is-up", buffer.readBoolean());
            }
        }
    }
}
