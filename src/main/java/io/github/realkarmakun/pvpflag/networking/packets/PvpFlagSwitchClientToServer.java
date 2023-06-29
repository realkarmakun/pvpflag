package io.github.realkarmakun.pvpflag.networking.packets;

import io.github.realkarmakun.pvpflag.components.PlayerFlagComponentRegistrar;
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
        PlayerFlagComponentRegistrar.FLAG_DATA.get(player).switchState();
    }
}
