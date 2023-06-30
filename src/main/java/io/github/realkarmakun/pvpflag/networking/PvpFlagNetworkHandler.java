package io.github.realkarmakun.pvpflag.networking;

import io.github.realkarmakun.pvpflag.PvpFlagMod;
import io.github.realkarmakun.pvpflag.networking.packets.PvpFlagSwitchClientToServer;
import io.github.realkarmakun.pvpflag.networking.packets.PvpFlagSyncRequestClientToServer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class PvpFlagNetworkHandler  {

    public static final ResourceLocation PVP_FLAG_SWITCH_ID = new ResourceLocation(PvpFlagMod.MOD_ID, "pvpflag_switch");
    public static final ResourceLocation PVP_FLAG_SYNC_REQ_ID = new ResourceLocation(PvpFlagMod.MOD_ID, "pvpflag_sync_request");

    public static void registerClientToServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PVP_FLAG_SWITCH_ID, PvpFlagSwitchClientToServer::receive);
        ServerPlayNetworking.registerGlobalReceiver(PVP_FLAG_SYNC_REQ_ID, PvpFlagSyncRequestClientToServer::receive);
    }

    /*public static void registerServerToClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(PVP_FLAG_SYNC_ID, PvpFlagSyncServerToClient::receive);
    }*/
}
