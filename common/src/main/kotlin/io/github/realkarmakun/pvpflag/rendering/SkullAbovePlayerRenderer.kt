package io.github.realkarmakun.pvpflag.rendering

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import io.github.realkarmakun.pvpflag.Constants
import io.github.realkarmakun.pvpflag.PvpFlagClientState
import io.github.realkarmakun.pvpflag.PvpFlagConfig
import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay
import io.github.realkarmakun.pvpflag.location
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f

object SkullAbovePlayerRenderer {
    @JvmField
    val SKULL_TEXTURE: ResourceLocation = "textures/basic_skull.png".location()

    @JvmStatic
    fun renderSkull(
        dispatcher: EntityRenderDispatcher,
        player: AbstractClientPlayer,
        poseStack: PoseStack
    ) {
        if (player.isLocalPlayer || player.isSpectator) return
        if (Minecraft.getInstance().options.hideGui) return

        val flagEnabled = PvpFlagClientState.getFlag(player.uuid)
        if (!flagEnabled) return

        val maxDistance = PvpFlagConfig.ABOVE_PLAYER_MAX_DISTANCE.get()
        if (dispatcher.distanceToSqr(player) > maxDistance * maxDistance) return

        Constants.LOG.info("[pvpflag] renderSkull called for {}", player.name.string)

        val baseAlpha = if (player.isDiscrete) 0.3f else 1.0f
        val alpha = if (PvpFlagClientState.isDisabling(player.uuid)) {
            val remaining = PvpFlagClientState.getRemainingTimeout(player.uuid)
            val client = Minecraft.getInstance()
            val gameTime = (client.level?.gameTime ?: 0L).toFloat()
            baseAlpha * PvpFlagHudOverlay.computePulseAlpha(gameTime, remaining)
        } else {
            baseAlpha
        }

        val entityHeight = player.bbHeight + 0.5
        val size = PvpFlagConfig.ABOVE_PLAYER_SKULL_SIZE.get()
        val skullX = PvpFlagConfig.ABOVE_PLAYER_SKULL_X.get().toFloat()
        val skullY = PvpFlagConfig.ABOVE_PLAYER_SKULL_Y_OFFSET.get().toFloat()
        val halfSize = size / 2.0f

        poseStack.pushPose()
        poseStack.translate(0.0, entityHeight, 0.0)
        poseStack.mulPose(dispatcher.cameraOrientation())
        poseStack.scale(-0.025f, -0.025f, 0.025f)

        val matrix = poseStack.last().pose()
        Constants.LOG.info("[pvpflag] tesselator draw matrix={}", matrix)

        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }
        RenderSystem.setShaderTexture(0, SKULL_TEXTURE)
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()

        val bufferBuilder = Tesselator.getInstance().begin(
            VertexFormat.Mode.QUADS,
            com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR
        )

        bufferBuilder.addVertex(matrix, skullX - halfSize, skullY - size, 0.0f).setUv(0.0f, 0.0f).setColor(255, 255, 255, 255)
        bufferBuilder.addVertex(matrix, skullX + halfSize, skullY - size, 0.0f).setUv(1.0f, 0.0f).setColor(255, 255, 255, 255)
        bufferBuilder.addVertex(matrix, skullX + halfSize, skullY, 0.0f).setUv(1.0f, 1.0f).setColor(255, 255, 255, 255)
        bufferBuilder.addVertex(matrix, skullX - halfSize, skullY, 0.0f).setUv(0.0f, 1.0f).setColor(255, 255, 255, 255)

        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.disableBlend()

        poseStack.popPose()
    }
}
