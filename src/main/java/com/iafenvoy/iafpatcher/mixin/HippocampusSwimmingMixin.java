package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import net.minecraft.tags.FluidTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Registered only for 1.20.1, where this controller and its unconditional buoyancy were introduced. */
//? >=1.20 {
/*@Mixin(targets = "com.github.alexthe666.iceandfire.entity.EntityHippocampus$HippoMoveControl", remap = false)
public class HippocampusSwimmingMixin {
    @Shadow(remap = false)
    @Final
    private EntityHippocampus hippo;

    @Inject(method = "updateSpeed", at = @At("HEAD"), cancellable = true)
    private void iafpatcher$keepSwimmingDepth(CallbackInfo ci) {
        if (!this.hippo.isInWater()) return;
        // Active paths supply their own vertical steering. Only an idle, unmounted mount
        // close to the surface needs a small downward impulse; deeper water stays neutral.
        if (!this.hippo.isVehicle() && !this.hippo.isOrderedToSit() && this.hippo.getNavigation().isDone()
                && !this.hippo.level().getFluidState(this.hippo.blockPosition().above(2)).is(FluidTags.WATER)
                && this.hippo.level().getFluidState(this.hippo.blockPosition().below()).is(FluidTags.WATER)) {
            this.hippo.setDeltaMovement(this.hippo.getDeltaMovement().add(0, -0.015, 0));
        }
        ci.cancel();
    }
}
*///?}
