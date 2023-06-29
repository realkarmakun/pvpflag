package io.github.realkarmakun.pvpflag.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public class PlayerFlagComponent implements PlayerFlagComponentInterface, AutoSyncedComponent {
    public static final String nbtKey = "pvp-flag-status";
    private final Object provider;

    private boolean pvpFlag = false;

    public PlayerFlagComponent(Object provider) {
        this.provider = provider;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        setState(tag.getBoolean(nbtKey));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean(nbtKey, peekState());
    }

    @Override
    public boolean switchState() {
        setState(!this.pvpFlag);
        return this.pvpFlag;
    }

    @Override
    public boolean peekState() {
        return this.pvpFlag;
    }

    @Override
    public void setState(boolean newState) {
        this.pvpFlag = newState;
        PlayerFlagComponentRegistrar.FLAG_DATA.sync(provider);
    }
}
