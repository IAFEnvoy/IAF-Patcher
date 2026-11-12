package com.iafenvoy.iafpatcher.mixin.ai;

import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.entity.ai.PixieAIFlee;
//? >=1.17 {
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
//?} else {
/*import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PixieAIFlee.class, remap = false)
public class PixieAIFleeMixin {
    @Shadow
    protected EntityPixie pixie;
    @Shadow
    protected Entity closestLivingEntity;
    @Shadow
    private /*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/ hidePlace;

    @Inject(method = "canContinueToUse", remap = true, at = @At("HEAD"), cancellable = true)
    private void iafpatcher$fleeUntilArrival(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.hidePlace != null && this.closestLivingEntity != null && this.closestLivingEntity.isAlive()
                && !this.pixie.isTame() && !this.pixie.getMainHandItem().isEmpty() && this.pixie.distanceToSqr(this.hidePlace) > 2);
    }
}
