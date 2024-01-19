package io.github.realkarmakun.pvpflag;

import io.github.realkarmakun.pvpflag.events.OnPlayerHurtEvent;
import io.github.realkarmakun.pvpflag.events.PlayerHurtEventCallback;
import io.github.realkarmakun.pvpflag.networking.PvpFlagNetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpFlagMod implements ModInitializer {
    public static final String MOD_ID = "pvpflag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final GameRules.Key<GameRules.BooleanValue> PEACEFUL_INDOMITABILITY =
            GameRuleRegistry.register("peacefulIndomitability", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

    @Override
    public void onInitialize() {

        PvpFlagNetworkHandler.registerClientToServerPackets();

        PlayerHurtEventCallback.EVENT.register(new OnPlayerHurtEvent());

    }
}
