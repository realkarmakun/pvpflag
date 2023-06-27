package io.github.realkarmakun.pvpflag.networking.packets;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class PvpFlagSyncServerToClient {

    public static void receive(
            Minecraft client,
            ClientPacketListener handler,
            FriendlyByteBuf buffer,
            PacketSender responseSender) {
        if (client.player instanceof PersistentEntityData player) {
            player.getPersistentData().putBoolean("is-up", buffer.readBoolean());
        }
    }
}
