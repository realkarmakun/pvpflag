package io.github.realkarmakun.pvpflag.keybinds

import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object PvpFlagKeybinds {
    const val CATEGORY = "key.category.pvpflag"
    const val KEY = "key.pvpflag.switch_pvp"

    private var ticksHeld = 0

    lateinit var keyMapping: KeyMapping

    @JvmStatic
    fun createKeyMapping(): KeyMapping {
        keyMapping = KeyMapping(KEY, GLFW.GLFW_KEY_K, CATEGORY)
        return keyMapping
    }

    @JvmStatic
    fun onClientTick(sendSwitchPacket: () -> Unit) {
        if (keyMapping.consumeClick()) {
            ticksHeld++
        } else if (ticksHeld != 0) {
            sendSwitchPacket()
            ticksHeld = 0
        }
    }
}
