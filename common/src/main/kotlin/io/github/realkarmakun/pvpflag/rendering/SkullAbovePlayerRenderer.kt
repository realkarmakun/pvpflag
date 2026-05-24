package io.github.realkarmakun.pvpflag.rendering

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Matrix4f
import io.github.realkarmakun.pvpflag.Constants
import io.github.realkarmakun.pvpflag.PvpFlagClientState
import io.github.realkarmakun.pvpflag.PvpFlagConfig
import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay
import net.minecraft.client.Minecraft
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.resources.ResourceLocation
import org.lwjgl.opengl.GL11

object SkullAbovePlayerRenderer {
    @JvmField
    val SKULL_TEXTURE: ResourceLocation = ResourceLocation(Constants.MOD_ID, "textures/basic_skull.png")

    @JvmStatic
    fun renderSkull(
        dispatcher: EntityRenderDispatcher,
        abstractPlayer: AbstractClientPlayer,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        if (!shouldRender(abstractPlayer)) return

        val flagEnabled = PvpFlagClientState.getFlag(abstractPlayer.uuid)
        if (!flagEnabled) return

        val maxDistance = PvpFlagConfig.ABOVE_PLAYER_MAX_DISTANCE.get()
        val squareDistance = dispatcher.distanceToSqr(abstractPlayer)
        if (squareDistance > maxDistance * maxDistance) return

        val font = Minecraft.getInstance().font
        val x = font.width(abstractPlayer.displayName)
        val y = (if ("deadmau5" == abstractPlayer.name.string) -28 else -12) + PvpFlagConfig.ABOVE_PLAYER_SKULL_Y_OFFSET.get()
        val entityHeight = abstractPlayer.bbHeight + 0.5f

        val baseAlpha = if (abstractPlayer.isDiscrete) 0.3f else 1.0f
        val alpha = if (PvpFlagClientState.isDisabling(abstractPlayer.uuid)) {
            val remaining = PvpFlagClientState.getRemainingTimeout(abstractPlayer.uuid)
            val client = Minecraft.getInstance()
            val gameTime = (client.level?.gameTime ?: 0L) + client.deltaFrameTime
            baseAlpha * PvpFlagHudOverlay.computePulseAlpha(gameTime, remaining)
        } else {
            baseAlpha
        }

        poseStack.pushPose()
        poseStack.translate(0.0, entityHeight.toDouble(), 0.0)
        poseStack.mulPose(dispatcher.cameraOrientation())
        poseStack.scale(-0.025f, -0.025f, 0.025f)

        val depthTestWasEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()

        renderSkullIcon(poseStack, x, y, alpha)

        if (depthTestWasEnabled) {
            RenderSystem.enableDepthTest()
        } else {
            RenderSystem.disableDepthTest()
        }

        poseStack.popPose()
    }

    private fun shouldRender(abstractPlayer: AbstractClientPlayer): Boolean {
        return !abstractPlayer.isLocalPlayer && !abstractPlayer.isSpectator && !Minecraft.getInstance().options.hideGui
    }

    private fun renderSkullIcon(poseStack: PoseStack, x: Int, y: Int, alpha: Float) {
        val skullX = PvpFlagConfig.ABOVE_PLAYER_SKULL_X.get()
        val size = PvpFlagConfig.ABOVE_PLAYER_SKULL_SIZE.get()

        poseStack.pushPose()
        poseStack.scale(1.0f, 1.0f, 1.0f)
        drawQuad(SKULL_TEXTURE, poseStack, skullX, y, size, size, 0f, 1f, 0f, 1f, alpha)
        poseStack.popPose()
    }

    private fun drawQuad(
        icon: ResourceLocation, poseStack: PoseStack,
        x: Int, y: Int, w: Int, h: Int,
        u0: Float, u1: Float, v0: Float, v1: Float, alpha: Float
    ) {
        val matrix: Matrix4f = poseStack.last().pose()

        Minecraft.getInstance().textureManager.getTexture(icon).setFilter(false, false)
        RenderSystem.setShaderTexture(0, icon)
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader)

        val bufferBuilder: BufferBuilder = Tesselator.getInstance().builder
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        bufferBuilder.vertex(matrix, x.toFloat(), (y + h).toFloat(), 0f).uv(u0, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex()
        bufferBuilder.vertex(matrix, (x + w).toFloat(), (y + h).toFloat(), 0f).uv(u1, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex()
        bufferBuilder.vertex(matrix, (x + w).toFloat(), y.toFloat(), 0f).uv(u1, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex()
        bufferBuilder.vertex(matrix, x.toFloat(), y.toFloat(), 0f).uv(u0, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex()
        BufferUploader.drawWithShader(bufferBuilder.end())
    }
}
