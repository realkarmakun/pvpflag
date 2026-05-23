package io.github.realkarmakun.pvpflag

import net.minecraftforge.common.ForgeConfigSpec

object PvpFlagConfig {
    enum class FlagLogic {
        MUTUAL,
        PERMISSIVE
    }

    @JvmField
    val SPEC: ForgeConfigSpec
    @JvmField
    val FLAG_LOGIC: ForgeConfigSpec.EnumValue<FlagLogic>
    @JvmField
    val DISABLE_TIMEOUT_SECONDS: ForgeConfigSpec.DoubleValue

    @JvmField
    val CLIENT_SPEC: ForgeConfigSpec
    @JvmField
    val HUD_SKULL_X_OFFSET: ForgeConfigSpec.IntValue
    @JvmField
    val HUD_SKULL_Y_OFFSET: ForgeConfigSpec.IntValue
    @JvmField
    val ABOVE_PLAYER_SKULL_X: ForgeConfigSpec.IntValue
    @JvmField
    val ABOVE_PLAYER_SKULL_Y_OFFSET: ForgeConfigSpec.IntValue
    @JvmField
    val ABOVE_PLAYER_SKULL_SIZE: ForgeConfigSpec.IntValue
    @JvmField
    val ABOVE_PLAYER_MAX_DISTANCE: ForgeConfigSpec.IntValue

    init {
        val builder = ForgeConfigSpec.Builder()
        builder.comment("PvP Flag configuration").push("general")

        FLAG_LOGIC = builder.comment(
            "How the PvP flag affects combat:",
            "MUTUAL - PvP only if BOTH players have their flag enabled",
            "PERMISSIVE - PvP blocked only if BOTH players have their flag disabled"
        ).defineEnum("flagLogic", FlagLogic.PERMISSIVE)

        DISABLE_TIMEOUT_SECONDS = builder.comment(
            "Timeout in seconds before the PvP flag is actually disabled.",
            "During this time, taking or dealing PvP damage cancels the disable.",
            "Set to 0 to disable instantly."
        ).defineInRange("disableTimeoutSeconds", 5.0, 0.0, 60.0)

        builder.pop()
        SPEC = builder.build()

        val clientBuilder = ForgeConfigSpec.Builder()
        clientBuilder.comment("Client-side PvP Flag rendering configuration").push("hud")

        HUD_SKULL_X_OFFSET = clientBuilder.comment(
            "Horizontal offset of the skull icon on the HUD, relative to the crosshair center"
        ).defineInRange("hudSkullXOffset", 95, -10000, 10000)

        HUD_SKULL_Y_OFFSET = clientBuilder.comment(
            "Vertical offset of the skull icon on the HUD, relative to the bottom of the screen"
        ).defineInRange("hudSkullYOffset", 19, 0, 10000)

        clientBuilder.pop().push("above_player")

        ABOVE_PLAYER_SKULL_X = clientBuilder.comment(
            "Horizontal offset of the skull icon rendered above other players"
        ).defineInRange("abovePlayerSkullX", -3, -10000, 10000)

        ABOVE_PLAYER_SKULL_Y_OFFSET = clientBuilder.comment(
            "Vertical offset added to the name tag Y position for the skull above players"
        ).defineInRange("abovePlayerSkullYOffset", 0, -10000, 10000)

        ABOVE_PLAYER_SKULL_SIZE = clientBuilder.comment(
            "Size (width and height) of the skull icon rendered above players"
        ).defineInRange("abovePlayerSkullSize", 8, 1, 64)

        ABOVE_PLAYER_MAX_DISTANCE = clientBuilder.comment(
            "Maximum distance (in blocks) at which the skull icon is rendered above players"
        ).defineInRange("abovePlayerMaxDistance", 64, 1, 256)

        clientBuilder.pop()
        CLIENT_SPEC = clientBuilder.build()
    }
}
