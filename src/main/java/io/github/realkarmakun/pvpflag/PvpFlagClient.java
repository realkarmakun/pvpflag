package io.github.realkarmakun.pvpflag;

import io.github.realkarmakun.pvpflag.hud.PvpFlagHudOverlay;
import io.github.realkarmakun.pvpflag.keybinds.PvpFlagInputHandler;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;

public class PvpFlagClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        PvpFlagInputHandler.register();

        PvpFlagNetworkHandler.registerServerToClientPackets();

        HudRenderCallback.EVENT.register(new PvpFlagHudOverlay());

        /*LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper) -> {
            if (entityType.equals(EntityType.PLAYER)) {
                registrationHelper.register(new MyFeatureRenderer((PlayerEntityModel) entityRenderer));
            }
        });*/
    }
}
