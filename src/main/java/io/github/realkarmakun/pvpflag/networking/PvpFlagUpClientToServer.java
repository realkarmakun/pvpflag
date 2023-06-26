package io.github.realkarmakun.pvpflag.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

public class PvpFlagUpClientToServer {
    public static void receive(MinecraftServer server,
                               ServerPlayer player,
                               ServerGamePacketListenerImpl netHandler,
                               FriendlyByteBuf buffer,
                               PacketSender responseSender) {
        // Server-side
        // Look into player data
        // check pvp flag status
        // Switch it up
        EntityType.COW.spawn(player.serverLevel(), player.blockPosition(), MobSpawnType.TRIGGERED);
    }
}
