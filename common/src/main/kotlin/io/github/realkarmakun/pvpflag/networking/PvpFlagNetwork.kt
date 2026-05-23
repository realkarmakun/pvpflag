package io.github.realkarmakun.pvpflag.networking

import io.github.realkarmakun.pvpflag.PvpFlagClientState
import io.github.realkarmakun.pvpflag.PvpFlagState
import io.github.realkarmakun.pvpflag.PlayerFlagEntry
import io.github.realkarmakun.pvpflag.location
import io.netty.buffer.Unpooled
import lol.bai.badpackets.api.C2SPacketReceiver
import lol.bai.badpackets.api.PacketSender
import lol.bai.badpackets.api.S2CPacketReceiver
import lol.bai.badpackets.api.event.PacketSenderReadyCallback
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

object PvpFlagNetwork {
    @JvmField
    val SWITCH_ID: ResourceLocation = "switch".location()

    @JvmField
    val FLAG_SYNC_ID: ResourceLocation = "flag_sync".location()

    const val STATE_DISABLED: Byte = 0
    const val STATE_ENABLED: Byte = 1
    const val STATE_DISABLING: Byte = 2

    fun registerServer() {
        C2SPacketReceiver.register(SWITCH_ID) { server, player, _, _, _ ->
            server.execute { handleSwitch(player) }
        }

        PacketSenderReadyCallback.registerServer { handler, sender, server ->
            val joiningPlayer = handler.player
            val state = PvpFlagState.get(joiningPlayer.serverLevel())
            for (serverPlayer in server.playerList.players) {
                val entry = state.getEntry(serverPlayer.uuid)
                if (!entry.enabled) continue

                if (serverPlayer.uuid == joiningPlayer.uuid && entry.isDisabling) {
                    sendDisablingSync(sender, serverPlayer.uuid, entry.remainingSeconds())
                } else {
                    sendFlagSync(sender, serverPlayer.uuid, STATE_ENABLED)
                }
            }
        }
    }

    fun registerClient() {
        S2CPacketReceiver.register(FLAG_SYNC_ID) { client, _, buf, _ ->
            val uuid = buf.readUUID()
            val stateByte = buf.readByte()
            val remainingSeconds = if (stateByte == STATE_DISABLING) buf.readFloat() else 0f
            client.execute {
                PvpFlagClientState.setFlag(uuid, stateByte, remainingSeconds)
            }
        }

        PacketSenderReadyCallback.registerClient { _, _, _ ->
            PvpFlagClientState.clear()
        }
    }

    private fun createBuf(): FriendlyByteBuf = FriendlyByteBuf(Unpooled.buffer())

    @JvmStatic
    fun sendFlagSync(sender: PacketSender, uuid: UUID, stateByte: Byte) {
        val buf = createBuf()
        buf.writeUUID(uuid)
        buf.writeByte(stateByte.toInt())
        sender.send(FLAG_SYNC_ID, buf)
    }

    @JvmStatic
    fun sendDisablingSync(sender: PacketSender, uuid: UUID, remainingSeconds: Float) {
        val buf = createBuf()
        buf.writeUUID(uuid)
        buf.writeByte(STATE_DISABLING.toInt())
        buf.writeFloat(remainingSeconds)
        sender.send(FLAG_SYNC_ID, buf)
    }

    private fun handleSwitch(player: ServerPlayer) {
        val state = PvpFlagState.get(player.serverLevel())
        val newEntry = state.requestToggle(player.uuid)

        if (newEntry.isDisabling) {
            sendDisablingSync(PacketSender.s2c(player), player.uuid, newEntry.remainingSeconds())
        } else {
            broadcastFlagState(player.server, player.uuid, newEntry)
        }
    }

    fun broadcastFlagState(server: net.minecraft.server.MinecraftServer, uuid: UUID, entry: PlayerFlagEntry) {
        val stateByte: Byte = if (entry.enabled) STATE_ENABLED else STATE_DISABLED
        for (target in server.playerList.players) {
            sendFlagSync(PacketSender.s2c(target), uuid, stateByte)
        }
    }

    @JvmStatic
    fun sendFlagSyncToPlayer(player: ServerPlayer, uuid: UUID, stateByte: Byte) {
        sendFlagSync(PacketSender.s2c(player), uuid, stateByte)
    }

    fun sendSwitchPacket() {
        PacketSender.c2s().send(SWITCH_ID, createBuf())
    }
}
