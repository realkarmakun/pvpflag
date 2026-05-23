package io.github.realkarmakun.pvpflag

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import java.util.UUID
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork
import net.minecraft.core.HolderLookup

data class PlayerFlagEntry(
    val enabled: Boolean,
    val timeoutRemainingTicks: Int = 0
) {
    val isDisabling: Boolean get() = enabled && timeoutRemainingTicks > 0

    val isEffectivelyEnabled: Boolean get() = enabled

    fun remainingSeconds(): Float = if (isDisabling) timeoutRemainingTicks / 20f else 0f
}

class PvpFlagState : SavedData() {
    private val entries = mutableMapOf<UUID, PlayerFlagEntry>()

    fun getEntry(uuid: UUID): PlayerFlagEntry = entries.getOrDefault(uuid, PlayerFlagEntry(false))

    fun isEffectivelyEnabled(uuid: UUID): Boolean = getEntry(uuid).isEffectivelyEnabled

    fun isDisabling(uuid: UUID): Boolean = getEntry(uuid).isDisabling

    fun requestToggle(uuid: UUID): PlayerFlagEntry {
        val current = getEntry(uuid)
        val newEntry = when {
            current.isDisabling -> PlayerFlagEntry(enabled = true)
            current.enabled -> {
                val timeoutSeconds = PvpFlagConfig.DISABLE_TIMEOUT_SECONDS.get()
                if (timeoutSeconds <= 0.0) {
                    PlayerFlagEntry(enabled = false)
                } else {
                    PlayerFlagEntry(enabled = true, timeoutRemainingTicks = (timeoutSeconds * 20.0).toInt())
                }
            }
            else -> PlayerFlagEntry(enabled = true)
        }
        entries[uuid] = newEntry
        setDirty()
        return newEntry
    }

    fun cancelTimeout(uuid: UUID): PlayerFlagEntry? {
        val current = getEntry(uuid)
        if (!current.isDisabling) return null
        val newEntry = PlayerFlagEntry(enabled = true)
        entries[uuid] = newEntry
        setDirty()
        return newEntry
    }

    fun tickServer(server: MinecraftServer) {
        val toExpire = mutableListOf<UUID>()

        for ((uuid, entry) in entries) {
            if (!entry.isDisabling) continue
            if (server.playerList.getPlayer(uuid) == null) continue

            val newRemaining = entry.timeoutRemainingTicks - 1
            if (newRemaining <= 0) {
                toExpire.add(uuid)
            } else {
                entries[uuid] = entry.copy(timeoutRemainingTicks = newRemaining)
            }
        }

        if (toExpire.isNotEmpty() || entries.any { it.value.isDisabling }) {
            setDirty()
        }

        for (uuid in toExpire) {
            entries[uuid] = PlayerFlagEntry(enabled = false)
            PvpFlagNetwork.broadcastFlagState(server, uuid, entries[uuid]!!)
        }
    }

    override fun save(tag: CompoundTag, p1: HolderLookup.Provider): CompoundTag {
        for ((uuid, entry) in entries) {
            val entryTag = CompoundTag()
            entryTag.putBoolean("enabled", entry.enabled)
            if (entry.timeoutRemainingTicks > 0) {
                entryTag.putInt("timeoutRemainingTicks", entry.timeoutRemainingTicks)
            }
            tag.put(uuid.toString(), entryTag)
        }
        return tag
    }

    companion object {
        private const val DATA_NAME = "pvpflag_state"

        @JvmStatic
        val FACTORY: SavedData.Factory<PvpFlagState> = SavedData.Factory(
            ::PvpFlagState,
            { tag, _ -> load(tag) },
            null
        )

        @JvmStatic
        fun load(tag: CompoundTag): PvpFlagState {
            val state = PvpFlagState()
            for (key in tag.allKeys) {
                try {
                    val uuid = UUID.fromString(key)
                    val entryTag = tag.get(key)
                    if (entryTag is CompoundTag) {
                        val enabled = entryTag.getBoolean("enabled")
                        val timeout = entryTag.getInt("timeoutRemainingTicks")
                        if (enabled || timeout > 0) {
                            state.entries[uuid] = PlayerFlagEntry(enabled, timeout)
                        }
                    } else {
                        state.entries[uuid] = PlayerFlagEntry(tag.getBoolean(key))
                    }
                } catch (_: Exception) {
                }
            }
            return state
        }

        @JvmStatic
        fun get(level: ServerLevel): PvpFlagState {
            return level.server.overworld().dataStorage.computeIfAbsent(FACTORY, DATA_NAME)
        }
    }
}
