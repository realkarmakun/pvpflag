package io.github.realkarmakun.pvpflag.networking.packets;

import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import io.github.realkarmakun.pvpflag.util.PvpFlagData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PvpFlagSwitchClientToServer {
    public static void receive(MinecraftServer server,
                               ServerPlayer player,
                               ServerGamePacketListenerImpl netHandler,
                               FriendlyByteBuf buffer,
                               PacketSender responseSender) {
        if (player instanceof PersistentEntityData playerData) {
            PvpFlagData.switchStatus(playerData);
        }
    }
}
