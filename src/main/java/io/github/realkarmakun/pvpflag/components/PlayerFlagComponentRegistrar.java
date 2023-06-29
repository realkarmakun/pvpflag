package io.github.realkarmakun.pvpflag.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.realkarmakun.pvpflag.PvpFlagMod;
import net.minecraft.resources.ResourceLocation;

public final class PlayerFlagComponentRegistrar implements EntityComponentInitializer {
    public static final ComponentKey<PlayerFlagComponent> FLAG_DATA =
            ComponentRegistry.getOrCreate(new ResourceLocation(PvpFlagMod.MOD_ID, "player-flag-data"), PlayerFlagComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(FLAG_DATA, PlayerFlagComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }
}
