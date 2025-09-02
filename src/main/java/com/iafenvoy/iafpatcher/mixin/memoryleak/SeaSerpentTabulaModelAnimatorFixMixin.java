package com.iafenvoy.iafpatcher.mixin.memoryleak;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.model.animator.SeaSerpentTabulaModelAnimator;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SeaSerpentTabulaModelAnimator.class, remap = false)
public class SeaSerpentTabulaModelAnimatorFixMixin {
    @Inject(method = "animate", at = @At("RETURN"))
    private void clearCache(TabulaModel model, EntitySeaSerpent entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, CallbackInfo ci) {
        model.llibAnimator.update(null);
    }
}
