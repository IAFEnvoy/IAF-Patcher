package com.iafenvoy.iafpatcher.mixin.ai;

import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.github.alexthe666.iceandfire.entity.ai.EntitySheepAIFollowCyclops;
//? >=1.17 {
import net.minecraft.world.entity.animal.Animal;
//?} else {
/*import net.minecraft.entity.passive.AnimalEntity;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntitySheepAIFollowCyclops.class, remap = false)
public class SheepFollowCyclopsMixin {
    @Shadow
    /*? >=1.17 {*/Animal/*?} else {*//*AnimalEntity*//*?}*/ childAnimal;
    @Shadow
    EntityCyclops cyclops;

    @Inject(method = "canContinueToUse", remap = true, at = @At("HEAD"), cancellable = true)
    private void iafpatcher$followLivingCyclops(CallbackInfoReturnable<Boolean> cir) {
        double distance = this.cyclops == null ? 0 : this.childAnimal.distanceToSqr(this.cyclops);
        cir.setReturnValue(this.cyclops != null && this.cyclops.isAlive() && distance >= 9 && distance <= 256);
    }
}
