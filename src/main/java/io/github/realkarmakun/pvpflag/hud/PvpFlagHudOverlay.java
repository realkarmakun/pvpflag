package io.github.realkarmakun.pvpflag.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.realkarmakun.pvpflag.PvpFlagMod;
import io.github.realkarmakun.pvpflag.components.PlayerFlagComponentRegistrar;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class PvpFlagHudOverlay implements HudRenderCallback {
    public static final ResourceLocation PVP_FLAG_BASIC = new ResourceLocation(PvpFlagMod.MOD_ID, "textures/basic_skull.png");

    @Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        PlayerFlagComponentRegistrar.FLAG_DATA.maybeGet(client.player).ifPresent(pvpFlagPlayerComponent -> {
            if (pvpFlagPlayerComponent.peekState()) {
                final var width = client.getWindow().getGuiScaledWidth();
                final var height = client.getWindow().getGuiScaledHeight();
                final var x = width / 2;
                final var y = height;

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, PVP_FLAG_BASIC);
                drawContext.blit(PVP_FLAG_BASIC, x + 95 , y - 19, 0, 0, 16, 16, 16, 16);
            }
        });

    }
}
