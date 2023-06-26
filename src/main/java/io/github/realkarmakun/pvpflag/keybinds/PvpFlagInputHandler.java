package io.github.realkarmakun.pvpflag.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class PvpFlagInputHandler {
    public static final String PVP_FLAG_KEY_CATEGORY = "key.category.pvpflag";
    public static final String PVP_FLAG_KEY = "key.pvpflag.switch_pvp";

    public static KeyMapping switchPvpStatus;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (switchPvpStatus.isDown()) {
                ClientPlayNetworking.send(PvpFlagNetworkHandler.PVP_FLAG_UP_ID, PacketByteBufs.create());
            }
        });
    }

    public static void register() {
        switchPvpStatus = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                PVP_FLAG_KEY,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                PVP_FLAG_KEY_CATEGORY
        ));

        registerKeyInputs();
    }
}
