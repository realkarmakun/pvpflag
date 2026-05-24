package io.github.realkarmakun.pvpflag.hud

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import io.github.realkarmakun.pvpflag.Constants
import io.github.realkarmakun.pvpflag.PvpFlagClientState
import io.github.realkarmakun.pvpflag.PvpFlagConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import kotlin.math.sin

object PvpFlagHudOverlay {
    @JvmField
    val SKULL_TEXTURE: ResourceLocation = ResourceLocation(Constants.MOD_ID, "textures/basic_skull.png")

    @JvmStatic
    fun render(poseStack: PoseStack, tickDelta: Float) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        if (client.options.hideGui) return
        if (!PvpFlagClientState.getFlag(player.uuid)) return

        val width = client.window.guiScaledWidth
        val height = client.window.guiScaledHeight

        val xOffset = PvpFlagConfig.HUD_SKULL_X_OFFSET.get()
        val yOffset = PvpFlagConfig.HUD_SKULL_Y_OFFSET.get()

        val remaining = PvpFlagClientState.getRemainingTimeout(player.uuid)
        val alpha = if (PvpFlagClientState.isDisabling(player.uuid)) {
            val gameTime = (client.level?.gameTime ?: 0L) + tickDelta
            computePulseAlpha(gameTime, remaining)
        } else {
            1.0f
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShaderTexture(0, SKULL_TEXTURE)
        GuiComponent.blit(poseStack, width / 2 + xOffset, height - yOffset, 0, 0.0f, 0.0f, 16, 16, 16, 16)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    fun computePulseAlpha(gameTimeTicks: Float, remainingSeconds: Float): Float {
        return (sin(gameTimeTicks * 0.5) * 0.5 + 0.5)
            .toFloat()
            .coerceIn(0f, 1f)
    }
}
