package io.github.realkarmakun.pvpflag.networking.packets;

import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import io.github.realkarmakun.pvpflag.util.PvpFlagData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
            final var newState = PvpFlagData.switchStatus(playerData);
            PlayerLookup.all(server).forEach(serverPlayer -> {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeUUID(player.getUUID());
                buf.writeBoolean(newState);
                ServerPlayNetworking.send(serverPlayer, PvpFlagNetworkHandler.PVP_FLAG_SYNC_ID, buf);
            });
        }
    }
}
