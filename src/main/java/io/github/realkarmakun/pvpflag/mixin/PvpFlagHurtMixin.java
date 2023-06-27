package io.github.realkarmakun.pvpflag.mixin;

import io.github.realkarmakun.pvpflag.events.PlayerHurtEventCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PvpFlagHurtMixin {

    // DamageSource#getEntity = causing entity,
    // DamageSource#getDirect = entity, that was directly causing damage, e.g. arrow
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void injectHurtPlayer(final DamageSource damageSource, final float damage, final CallbackInfoReturnable<Boolean> cir) {
        InteractionResult result = PlayerHurtEventCallback.EVENT.invoker().hurt((Player) (Object) this, damageSource, damage);
        if (result.equals(InteractionResult.FAIL)) {
            cir.setReturnValue(false);
        }
    }
}
