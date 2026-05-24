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

/**
 * Hooks into {@link PlayerRenderer} to draw the PvP skull billboard above players.
 *
 * <p>We inject at the tail of {@code renderNameTag} rather than {@code render} because:
 * <ul>
 *   <li>At that point the pose stack is in entity-local space (origin = player feet),
 *       which is exactly the coordinate system our billboard transform expects.</li>
 *   <li>The {@code bufferSource} and {@code packedLight} arguments are already in scope,
 *       so we can forward them to the renderer without any extra lookups.</li>
 *   <li>The skull is rendered in the same pass as name tags, so draw ordering and
 *       translucency sorting are handled consistently by the shared buffer source.</li>
 * </ul>
 *
 * <p>Note: {@code renderNameTag} is only called when {@code shouldShowName} returns true
 * (i.e. the player is within name-tag range and the name tag is visible). The skull
 * therefore respects the same visibility rules as the name tag, which is intentional.
 */
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
    private void pvpflag$renderSkull(
        AbstractClientPlayer player,
        Component displayName,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        float partialTick,
        CallbackInfo ci
    ) {
        SkullAbovePlayerRenderer.renderSkull(
            this.entityRenderDispatcher,
            player,
            poseStack,
            bufferSource,
            packedLight
        );
    }
}
