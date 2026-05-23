package io.github.realkarmakun.pvpflag

import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import java.util.UUID

data class ClientFlagState(
    val enabled: Boolean,
    var remainingTimeout: Float = 0f
) {
    val isDisabling: Boolean get() = enabled && remainingTimeout > 0f
}

object PvpFlagClientState {
    private val flags = mutableMapOf<UUID, ClientFlagState>()

    @JvmStatic
    fun getFlag(uuid: UUID): Boolean = flags[uuid]?.enabled ?: false

    @JvmStatic
    fun isDisabling(uuid: UUID): Boolean = flags[uuid]?.isDisabling ?: false

    @JvmStatic
    fun getRemainingTimeout(uuid: UUID): Float = flags[uuid]?.remainingTimeout ?: 0f

    @JvmStatic
    fun setFlag(uuid: UUID, stateByte: Byte, remainingSeconds: Float = 0f) {
        val enabled = stateByte != PvpFlagNetwork.STATE_DISABLED
        flags[uuid] = ClientFlagState(
            enabled = enabled,
            remainingTimeout = if (stateByte == PvpFlagNetwork.STATE_DISABLING) remainingSeconds else 0f
        )
    }

    @JvmStatic
    fun tick() {
        for ((_, state) in flags) {
            if (state.remainingTimeout > 0f) {
                state.remainingTimeout -= 1f / 20f
                if (state.remainingTimeout < 0f) {
                    state.remainingTimeout = 0f
                }
            }
        }
    }

    @JvmStatic
    fun clear() {
        flags.clear()
    }
}
