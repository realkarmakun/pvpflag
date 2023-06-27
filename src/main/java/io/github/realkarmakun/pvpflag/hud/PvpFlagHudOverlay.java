package io.github.realkarmakun.pvpflag.hud;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.realkarmakun.pvpflag.PvpFlagMod;
import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class PvpFlagHudOverlay implements HudRenderCallback {
    private static final ResourceLocation PVP_FLAG_UP = new ResourceLocation(PvpFlagMod.MOD_ID, "textures/basic_skull.png");
    //private static final ResourceLocation PVPF_FLAG_DOWN;

    @Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
        int x = 0;
        int y = 0;
        Minecraft client = Minecraft.getInstance();
        final var width = client.getWindow().getGuiScaledWidth();
        final var height = client.getWindow().getGuiScaledHeight();
        x = width / 2;
        y = height;

        if (client.player instanceof PersistentEntityData player && player.getPersistentData().getBoolean("is-up")) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, PVP_FLAG_UP);
            drawContext.blit(PVP_FLAG_UP, x + 95 , y - 19, 0, 0, 16, 16, 16, 16);
        }


    }
}
