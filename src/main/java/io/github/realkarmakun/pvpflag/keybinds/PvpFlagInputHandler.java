package io.github.realkarmakun.pvpflag.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import javax.swing.text.JTextComponent;

public class PvpFlagInputHandler {
    public static final String PVP_FLAG_KEY_CATEGORY = "key.category.pvpflag";
    public static final String PVP_FLAG_KEY = "key.pvpflag.switch_pvp";

    private static int numberOfTicksHeld = 0;
    public static KeyMapping switchPvpStatus;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (switchPvpStatus.consumeClick()) {
                numberOfTicksHeld++;
            } else if (numberOfTicksHeld != 0) {
                ClientPlayNetworking.send(PvpFlagNetworkHandler.PVP_FLAG_SWITCH_ID, PacketByteBufs.create());
                numberOfTicksHeld = 0;
            }
        });
    }

    public static void register() {
        switchPvpStatus = KeyBindingHelper.registerKeyBinding(new ToggleKeyMapping(
                PVP_FLAG_KEY,
                GLFW.GLFW_KEY_K,
                PVP_FLAG_KEY_CATEGORY,
                () -> true
        ));

        registerKeyInputs();
    }
}
