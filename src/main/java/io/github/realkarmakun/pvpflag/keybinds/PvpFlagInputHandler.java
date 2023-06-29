package io.github.realkarmakun.pvpflag.keybinds;

import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class PvpFlagInputHandler {
    public static final String PVP_FLAG_KEY_CATEGORY = "key.category.pvpflag";
    public static final String PVP_FLAG_KEY = "key.pvpflag.switch_pvp";

    private static int numberOfTicksHeld = 0;
    public static KeyMapping switchPvpStatus;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (switchPvpStatus.consumeClick()) {
                numberOfTicksHeld++;
            } else if (numberOfTicksHeld != 0 && client.player != null) {
                ClientPlayNetworking.send(PvpFlagNetworkHandler.PVP_FLAG_SWITCH_ID, PacketByteBufs.create());
                //PlayerFlagComponentRegistrar.PLAYER_FLAG_DATA.get(client.player).switchState();
                numberOfTicksHeld = 0;
            }
        });
    }

    public static void register() {
        switchPvpStatus = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                PVP_FLAG_KEY,
                GLFW.GLFW_KEY_K,
                PVP_FLAG_KEY_CATEGORY
        ));

        registerKeyInputs();
    }
}
