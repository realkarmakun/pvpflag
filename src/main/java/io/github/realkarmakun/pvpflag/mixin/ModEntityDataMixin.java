package io.github.realkarmakun.pvpflag.mixin;

import io.github.realkarmakun.pvpflag.PersistentEntityData;
import io.github.realkarmakun.pvpflag.PvpFlagMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class ModEntityDataMixin implements PersistentEntityData {
    private CompoundTag persistentData;

    @Override
    public CompoundTag getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new CompoundTag();
        }
        return persistentData;
    }

    @Inject(method = "save", at = @At("HEAD"))
    protected void injectWriteMethod(CompoundTag compoundTag, CallbackInfoReturnable clInfo) {
        if (persistentData != null) {
            compoundTag.put("pvpflag.switch_status", persistentData);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    protected void injectWriteMethod(CompoundTag compoundTag, CallbackInfo clInfo) {
        if (compoundTag.contains("pvpflag.switch_status")) {
            persistentData = compoundTag.getCompound("pvpflag.switch_status");
        }
    }

}
