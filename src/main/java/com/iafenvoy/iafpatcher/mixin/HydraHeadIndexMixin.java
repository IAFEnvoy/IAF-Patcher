package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityHydra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityHydra.class, remap = false)
public abstract class HydraHeadIndexMixin {
    @Shadow
    private int lastHitHead;

    @Shadow
    public abstract int getHeadCount();

    @Inject(method = "hurt", remap = true, at = @At("HEAD"))
    private void iafpatcher$validateHeadIndex(CallbackInfoReturnable<Boolean> cir) {
        this.lastHitHead = Math.max(0, Math.min(this.lastHitHead, this.getHeadCount() - 1));
    }
}
