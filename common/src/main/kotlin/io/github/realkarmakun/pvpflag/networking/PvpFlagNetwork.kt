package io.github.realkarmakun.pvpflag.networking

import io.github.realkarmakun.pvpflag.PvpFlagClientState
import io.github.realkarmakun.pvpflag.PvpFlagState
import io.github.realkarmakun.pvpflag.PlayerFlagEntry
import io.github.realkarmakun.pvpflag.location
import lol.bai.badpackets.api.PacketSender
import lol.bai.badpackets.api.play.PlayPackets
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class SwitchPayload private constructor() : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<SwitchPayload> = TYPE

    companion object {
        @JvmField
        val ID: ResourceLocation = "switch".location()

        @JvmField
        val TYPE: CustomPacketPayload.Type<SwitchPayload> = CustomPacketPayload.Type(ID)

        @JvmField
        val CODEC: StreamCodec<FriendlyByteBuf, SwitchPayload> = CustomPacketPayload.codec(
            { _: SwitchPayload, _: FriendlyByteBuf -> },
            { SwitchPayload() }
        )

        @JvmField
        val INSTANCE = SwitchPayload()
    }
}

class FlagSyncPayload(
    val uuid: UUID,
    val stateByte: Byte,
    val remainingSeconds: Float
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<FlagSyncPayload> = TYPE

    companion object {
        @JvmField
        val ID: ResourceLocation = "flag_sync".location()

        @JvmField
        val TYPE: CustomPacketPayload.Type<FlagSyncPayload> = CustomPacketPayload.Type(ID)

        @JvmField
        val CODEC: StreamCodec<FriendlyByteBuf, FlagSyncPayload> = CustomPacketPayload.codec(
            { payload, buf ->
                buf.writeUUID(payload.uuid)
                buf.writeByte(payload.stateByte.toInt())
                if (payload.stateByte == PvpFlagNetwork.STATE_DISABLING) {
                    buf.writeFloat(payload.remainingSeconds)
                }
            },
            { buf ->
                val uuid = buf.readUUID()
                val stateByte = buf.readByte()
                val remainingSeconds = if (stateByte == PvpFlagNetwork.STATE_DISABLING) buf.readFloat() else 0f
                FlagSyncPayload(uuid, stateByte, remainingSeconds)
            }
        )
    }
}

object PvpFlagNetwork {
    const val STATE_DISABLED: Byte = 0
    const val STATE_ENABLED: Byte = 1
    const val STATE_DISABLING: Byte = 2

    fun common() {
        PlayPackets.registerServerChannel(SwitchPayload.TYPE, SwitchPayload.CODEC)
        PlayPackets.registerClientChannel(FlagSyncPayload.TYPE, FlagSyncPayload.CODEC)

        PlayPackets.registerServerReceiver(SwitchPayload.TYPE) { context, _ ->
            val player = context.player()
            context.server().execute { handleSwitch(player) }
        }

        PlayPackets.registerServerReadyCallback { context ->
            val joiningPlayer = context.player()
            val server = context.server()
            val state = PvpFlagState.get(joiningPlayer.serverLevel())
            for (serverPlayer in server.playerList.players) {
                val entry = state.getEntry(serverPlayer.uuid)
                if (!entry.enabled) continue

                if (serverPlayer.uuid == joiningPlayer.uuid && entry.isDisabling) {
                    context.send(FlagSyncPayload(serverPlayer.uuid, STATE_DISABLING, entry.remainingSeconds()))
                } else {
                    context.send(FlagSyncPayload(serverPlayer.uuid, STATE_ENABLED, 0f))
                }
            }
        }
    }

    fun client() {
        PlayPackets.registerClientReceiver(FlagSyncPayload.TYPE) { context, payload ->
            context.client().execute {
                PvpFlagClientState.setFlag(payload.uuid, payload.stateByte, payload.remainingSeconds)
            }
        }

        PlayPackets.registerClientReadyCallback {
            PvpFlagClientState.clear()
        }
    }

    @JvmStatic
    fun sendFlagSync(sender: PacketSender, uuid: UUID, stateByte: Byte) {
        sender.send(FlagSyncPayload(uuid, stateByte, 0f))
    }

    @JvmStatic
    fun sendDisablingSync(sender: PacketSender, uuid: UUID, remainingSeconds: Float) {
        sender.send(FlagSyncPayload(uuid, STATE_DISABLING, remainingSeconds))
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
        PacketSender.c2s().send(SwitchPayload.INSTANCE)
    }
}
