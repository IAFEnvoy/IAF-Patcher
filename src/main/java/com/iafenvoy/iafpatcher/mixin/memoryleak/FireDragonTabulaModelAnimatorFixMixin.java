package com.iafenvoy.iafpatcher.mixin.memoryleak;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.model.animator.FireDragonTabulaModelAnimator;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = FireDragonTabulaModelAnimator.class, remap = false)
public class FireDragonTabulaModelAnimatorFixMixin {
    @Inject(method = "animate", at = @At("RETURN"))
    private void clearCache(TabulaModel model, EntityFireDragon entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, CallbackInfo ci) {
        model.llibAnimator.update(null);
    }
}
