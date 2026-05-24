package io.github.realkarmakun.pvpflag.rendering

import com.mojang.blaze3d.vertex.PoseStack
import io.github.realkarmakun.pvpflag.PvpFlagClientState
import io.github.realkarmakun.pvpflag.PvpFlagConfig
import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay
import io.github.realkarmakun.pvpflag.location
import net.minecraft.client.Minecraft
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.resources.ResourceLocation

object SkullAbovePlayerRenderer {
    @JvmField
    val SKULL_TEXTURE: ResourceLocation = "textures/basic_skull.png".location()

    /**
     * Renders the PvP skull icon as a camera-aligned billboard above [player]'s head.
     *
     * This is called from [PlayerRendererMixin] at the tail of `PlayerRenderer.renderNameTag`,
     * which is the most convenient hook for billboard overlays: the [poseStack] is already in
     * entity-local space (origin at the player's feet), and the [bufferSource] / [packedLight]
     * that vanilla used to draw the name tag are forwarded here so the skull participates in
     * the same render pass and uses the same lighting information.
     *
     * @param dispatcher Used to get the camera orientation quaternion and measure distance.
     * @param player     The remote player to draw above. Local player and spectators are skipped.
     * @param poseStack  Entity-local pose stack (origin = player feet) — we push/pop our own frame.
     * @param bufferSource Batched renderer shared with the rest of the entity render pass.
     * @param packedLight  Pre-computed lightmap UV for the player's block position (sky + block light).
     */
    @JvmStatic
    fun renderSkull(
        dispatcher: EntityRenderDispatcher,
        player: AbstractClientPlayer,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        // Never draw over the local player, spectators, or when the HUD is hidden (F1).
        if (player.isLocalPlayer || player.isSpectator) return
        if (Minecraft.getInstance().options.hideGui) return

        // Only show the skull when the player's PvP flag is active.
        val flagEnabled = PvpFlagClientState.getFlag(player.uuid)
        if (!flagEnabled) return

        // Skip players outside the configured render distance (cheap squared comparison).
        val maxDistance = PvpFlagConfig.ABOVE_PLAYER_MAX_DISTANCE.get()
        if (dispatcher.distanceToSqr(player) > maxDistance * maxDistance) return

        // --- Alpha ---
        // Sneaking players are shown at 30 % opacity (matches vanilla name tag behaviour).
        // While the flag is disabling (countdown running), the icon pulses via computePulseAlpha.
        val baseAlpha = if (player.isDiscrete) 0.3f else 1.0f
        val alpha = if (PvpFlagClientState.isDisabling(player.uuid)) {
            val remaining = PvpFlagClientState.getRemainingTimeout(player.uuid)
            val client = Minecraft.getInstance()
            val gameTime = (client.level?.gameTime ?: 0L).toFloat()
            baseAlpha * PvpFlagHudOverlay.computePulseAlpha(gameTime, remaining)
        } else {
            baseAlpha
        }

        // --- Layout ---
        // skullX / skullY are in billboard model space (see coordinate notes below).
        // skullY is the BOTTOM edge of the quad; skullY - size is the TOP edge.
        val entityHeight = player.bbHeight + 0.5   // matches vanilla renderNameTag attachment point
        val size    = PvpFlagConfig.ABOVE_PLAYER_SKULL_SIZE.get()
        val skullX  = PvpFlagConfig.ABOVE_PLAYER_SKULL_X.get().toFloat()
        val skullY  = PvpFlagConfig.ABOVE_PLAYER_SKULL_Y_OFFSET.get().toFloat()
        val halfSize = size / 2.0f

        // --- Billboard transform ---
        // We replicate the three-step transform that vanilla uses inside renderNameTag:
        //   1. translate  – move the origin up to the name-tag attachment point.
        //   2. mulPose    – align the local X/Y axes with the camera screen plane
        //                   (makes the quad always face the viewer regardless of yaw/pitch).
        //   3. scale      – convert model-space units to world-space metres.
        //
        // Scale notes (1.21.1):
        //   X = -0.025f  – the negative sign flips the X axis so that the quad winding order
        //                   becomes CCW when viewed from the front.  With a positive X scale the
        //                   quad would be CW (back-facing) and back-face culling would hide it.
        //                   Vanilla renderNameTag also uses -0.025f for X in 1.21.1.
        //   Y = -0.025f  – negative so that *decreasing* model Y moves *upward* in world space,
        //                   which is the convention used by name tag rendering.
        //   Z =  0.025f  – positive depth scale (only affects perspective, not visible geometry).
        poseStack.pushPose()
        poseStack.translate(0.0, entityHeight, 0.0)
        poseStack.mulPose(dispatcher.cameraOrientation())
        poseStack.scale(-0.025f, -0.025f, 0.025f)

        val matrix   = poseStack.last().pose()
        val alphaInt = (alpha * 255).toInt().coerceIn(0, 255)

        // --- Render type choice ---
        // We use RenderType.text rather than an entity render type for two reasons:
        //
        //  1. Lighting model: entity render types (e.g. entityTranslucentCull) apply a fixed
        //     directional light that shades faces differently depending on their world-space
        //     normal.  For a billboard the normal points along the camera direction, so the
        //     skull would brighten/darken as the camera pitches up and down.  RenderType.text
        //     uses POSITION_COLOR_TEX_LIGHTMAP which has NO directional component – brightness
        //     is determined entirely by packedLight (sky + block light), matching the name tag.
        //
        //  2. Vertex format: POSITION_COLOR_TEX_LIGHTMAP only needs position, colour, texture
        //     UV, and lightmap UV – no overlay or normal attributes required.
        val consumer = bufferSource.getBuffer(RenderType.text(SKULL_TEXTURE))

        // Quad vertices in CCW order (top-left → top-right → bottom-right → bottom-left).
        // UV (0,0) is the top-left corner of the texture; (1,1) is the bottom-right corner.
        // The Y axis is flipped by the scale above, so "skullY - size" (more negative) maps to
        // a higher position in world space than "skullY" (less negative / closer to zero).
        consumer.addVertex(matrix, skullX - halfSize, skullY - size, 0f)
            .setColor(255, 255, 255, alphaInt)
            .setUv(0f, 0f)
            .setLight(packedLight)

        consumer.addVertex(matrix, skullX + halfSize, skullY - size, 0f)
            .setColor(255, 255, 255, alphaInt)
            .setUv(1f, 0f)
            .setLight(packedLight)

        consumer.addVertex(matrix, skullX + halfSize, skullY, 0f)
            .setColor(255, 255, 255, alphaInt)
            .setUv(1f, 1f)
            .setLight(packedLight)

        consumer.addVertex(matrix, skullX - halfSize, skullY, 0f)
            .setColor(255, 255, 255, alphaInt)
            .setUv(0f, 1f)
            .setLight(packedLight)

        poseStack.popPose()
    }
}
