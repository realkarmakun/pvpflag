package io.github.realkarmakun.pvpflag.events;

import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import io.github.realkarmakun.pvpflag.util.PvpFlagData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public class OnPlayerHurtEvent implements PlayerHurtEventCallback {
    @Override
    public InteractionResult hurt(Player recivingPlayer, DamageSource damageSource, float damage) {
        if (recivingPlayer instanceof PersistentEntityData player && damageSource.getEntity() instanceof PersistentEntityData causingEntity && PvpFlagData.hasStatus(causingEntity)) {
            final var receiverFlag = PvpFlagData.getStatus(player);
            final var causerEntity = PvpFlagData.getStatus(causingEntity);
            if (!causerEntity && !receiverFlag) {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }
}
