package io.github.realkarmakun.pvpflag.networking.packets;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import io.github.realkarmakun.pvpflag.PvpFlagMod;
import io.github.realkarmakun.pvpflag.components.PlayerFlagComponentRegistrar;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PvpFlagSyncRequestClientToServer {
    public static void receive(MinecraftServer server,
                               ServerPlayer player,
                               ServerGamePacketListenerImpl netHandler,
                               FriendlyByteBuf buffer,
                               PacketSender responseSender) {
        PlayerLookup.all(server)
                .stream()
                .filter(serverPlayer -> PlayerFlagComponentRegistrar.FLAG_DATA.get(serverPlayer).peekState())
                .forEach(serverPlayer -> PlayerFlagComponentRegistrar.FLAG_DATA.syncWith(player, (ComponentProvider) serverPlayer));
    }
}
