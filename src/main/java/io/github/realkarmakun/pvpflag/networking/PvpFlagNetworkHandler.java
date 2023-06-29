package io.github.realkarmakun.pvpflag.networking;

import io.github.realkarmakun.pvpflag.PvpFlagMod;
import io.github.realkarmakun.pvpflag.networking.packets.PvpFlagSwitchClientToServer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class PvpFlagNetworkHandler  {

    public static final ResourceLocation PVP_FLAG_SWITCH_ID = new ResourceLocation(PvpFlagMod.MOD_ID, "pvpflagswitch");
    //public static final ResourceLocation PVP_FLAG_SYNC_ID = new ResourceLocation(PvpFlagMod.MOD_ID, "pvpflagsync");

    public static void registerClientToServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PVP_FLAG_SWITCH_ID, PvpFlagSwitchClientToServer::receive);
    }

    /*public static void registerServerToClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(PVP_FLAG_SYNC_ID, PvpFlagSyncServerToClient::receive);
    }*/
}
