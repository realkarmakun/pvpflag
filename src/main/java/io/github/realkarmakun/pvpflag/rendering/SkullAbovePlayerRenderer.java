package io.github.realkarmakun.pvpflag.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class SkullAbovePlayerRenderer {

    public static void renderSkull(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, AbstractClientPlayer abstractPlayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!shouldRender(abstractPlayer)) return;
        if (abstractPlayer instanceof PersistentEntityData playerData && playerData.getPersistentData().getBoolean("is-up")) {
            // Getting Rendered Dispatcher
            final var squareDistance = renderer.entityRenderDispatcher.distanceToSqr(abstractPlayer);

            // If entity too far from camera
            if (squareDistance > 4096.0) return;

            // Calculating x: half of nickname width + a bit of space before the skull
            final var x = (renderer.entityRenderDispatcher.font.width(abstractPlayer.getDisplayName()));
            // Calculating y: it's mostly the same
            int y = "deadmau5".equals(abstractPlayer.getName().getString()) ? -28 : -12;
            // TODO: config value to add y offset

            final var entityHeight = abstractPlayer.getNameTagOffsetY();

            final var currentAlpha = 1.0f;

            poseStack.pushPose();
            poseStack.translate(0.0D, entityHeight, 0.0D);
            poseStack.mulPose(renderer.entityRenderDispatcher.cameraOrientation());
            // NameTags are scaled with this value, we do the same thing
            poseStack.scale(-0.025F, -0.025F, 0.025F);

            final boolean depthTestWasEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            renderSkull(poseStack, x, y, abstractPlayer.isDiscrete() ? 0.3f * currentAlpha : currentAlpha);

            // Revert depth test to original state.
            if (depthTestWasEnabled) {
                RenderSystem.enableDepthTest();
            } else {
                RenderSystem.disableDepthTest();
            }

            poseStack.popPose();
        }
    }

    private static boolean shouldRender(AbstractClientPlayer abstractPlayer) {
        return !abstractPlayer.isLocalPlayer() && !abstractPlayer.isSpectator();
    }

    private static void renderSkull(PoseStack poseStack, int x, int y, float alpha) {
        poseStack.pushPose();
        poseStack.scale(1.0f, 1.0f, 1.0f);
        drawTexture(io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay.PVP_FLAG_UP, poseStack, -3, y, 8, 8, 0, 1, 0, 1, alpha);
        poseStack.popPose();
    }


    private static void drawTexture(ResourceLocation icon, PoseStack poseStack, int x, int y, int w, int h, float u0, float u1, float v0, float v1, float alpha) {
        Matrix4f matrix = poseStack.last().pose();

        Minecraft.getInstance().getTextureManager().getTexture(icon).setFilter(false, false);
        RenderSystem.setShaderTexture(0, icon);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, (float)x,			(float)(y + h),		0).uv(u0, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float)(x + w),	(float)(y + h),		0).uv(u1, v1).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float)(x + w),	(float)y,			0).uv(u1, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float)x,			(float)y,			0).uv(u0, v0).color(1.0f, 1.0f, 1.0f, alpha).endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
