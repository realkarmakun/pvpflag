package io.github.realkarmakun.pvpflag.events;

import io.github.realkarmakun.pvpflag.data.PvpFlagComponentRegistrar;
import io.github.realkarmakun.pvpflag.util.PersistentEntityData;
import io.github.realkarmakun.pvpflag.util.PvpFlagData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public class OnPlayerHurtEvent implements PlayerHurtEventCallback {
    @Override
    public InteractionResult hurt(Player recivingPlayer, DamageSource damageSource, float damage) {
        final var causingEntity = damageSource.getEntity();
        if (causingEntity != null && PvpFlagComponentRegistrar.PLAYER_FLAG_DATA.isProvidedBy(causingEntity)) {
            final var receiverFlag = PvpFlagComponentRegistrar.PLAYER_FLAG_DATA.get(recivingPlayer).peekState();
            final var causerFlag = PvpFlagComponentRegistrar.PLAYER_FLAG_DATA.get(causingEntity).peekState();
            if (!causerFlag && !receiverFlag) {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }
}
