package com.iafenvoy.iafpatcher.mixin.memoryleak;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.model.animator.DragonTabulaModelAnimator;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DragonTabulaModelAnimator.class, remap = false)
public class DragonTabulaModelAnimatorFixMixin<T extends EntityDragonBase> {
    @Inject(method = "animate", at = @At("RETURN"))
    private void clearCache(TabulaModel model, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, CallbackInfo ci) {
        model.llibAnimator.update(null);
    }
}
