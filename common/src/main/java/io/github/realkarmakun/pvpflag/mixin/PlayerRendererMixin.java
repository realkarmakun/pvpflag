package io.github.realkarmakun.pvpflag.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.realkarmakun.pvpflag.rendering.SkullAbovePlayerRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin
    extends LivingEntityRenderer<
        AbstractClientPlayer,
        PlayerModel<AbstractClientPlayer>
    >
{

    public PlayerRendererMixin(
        EntityRendererProvider.Context context,
        PlayerModel<AbstractClientPlayer> model,
        float shadowRadius
    ) {
        super(context, model, shadowRadius);
    }

    @Inject(
        method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At("RETURN")
    )
    private void pvpflag$renderSkull(AbstractClientPlayer player, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, CallbackInfo ci) {
        SkullAbovePlayerRenderer.renderSkull(this.entityRenderDispatcher, player, poseStack);
    }
}
