package io.github.realkarmakun.pvpflag.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

/**
 * Callback for player receiving damage
 * Called before game registers player getting hurt by some DamageSource
 * Upon return:
 * - SUCCESS cancels further processing and continues with normal damage process
 * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
 * - FAIL cancels further processing and does allow player to receive damage
 **/
public interface PlayerHurtEventCallback {
    Event<PlayerHurtEventCallback> EVENT = EventFactory.createArrayBacked(PlayerHurtEventCallback.class,
            (listeners) -> (player, damageSource, damage) -> {
                for (PlayerHurtEventCallback listener : listeners) {
                    InteractionResult result = listener.hurt(player, damageSource, damage);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult hurt(Player recivingPlayer, DamageSource damageSource, float damage);
}
