package io.github.realkarmakun.pvpflag.mixin;

import io.github.realkarmakun.pvpflag.PvpFlagConfig;
import io.github.realkarmakun.pvpflag.PvpFlagState;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PvpFlagHurtMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void pvpflag$onHurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        var causingEntity = damageSource.getEntity();
        Player self = (Player) (Object) this;

        if (causingEntity == null) return;
        if (!(causingEntity instanceof Player)) return;
        if (self.level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) self.level;
        PvpFlagState state = PvpFlagState.Companion.get(serverLevel);

        if (state.isDisabling(causingEntity.getUUID())) {
            state.cancelTimeout(causingEntity.getUUID());
            if (causingEntity instanceof ServerPlayer sp) {
                PvpFlagNetwork.sendFlagSyncToPlayer(sp, causingEntity.getUUID(), PvpFlagNetwork.STATE_ENABLED);
            }
        }
        if (state.isDisabling(self.getUUID())) {
            state.cancelTimeout(self.getUUID());
            if (self instanceof ServerPlayer sp) {
                PvpFlagNetwork.sendFlagSyncToPlayer(sp, self.getUUID(), PvpFlagNetwork.STATE_ENABLED);
            }
        }

        boolean receiverEnabled = state.isEffectivelyEnabled(self.getUUID());
        boolean causerEnabled = state.isEffectivelyEnabled(causingEntity.getUUID());

        PvpFlagConfig.FlagLogic logic = PvpFlagConfig.FLAG_LOGIC.get();

        if (logic == PvpFlagConfig.FlagLogic.MUTUAL) {
            if (!causerEnabled || !receiverEnabled) {
                cir.setReturnValue(false);
            }
        } else {
            if (!causerEnabled && !receiverEnabled) {
                cir.setReturnValue(false);
            }
        }
    }
}
