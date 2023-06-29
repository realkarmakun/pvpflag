package io.github.realkarmakun.pvpflag.networking.packets;

import io.github.realkarmakun.pvpflag.data.PvpFlagComponentRegistrar;
import io.github.realkarmakun.pvpflag.data.PvpFlagPlayerComponent;
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
        PvpFlagComponentRegistrar.PLAYER_FLAG_DATA.get(player).switchState();
    }
}
