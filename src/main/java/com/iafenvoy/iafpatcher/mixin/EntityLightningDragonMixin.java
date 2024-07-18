package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityLightningDragon.class, remap = false)
public class EntityLightningDragonMixin {
    @Redirect(method = "stimulateFire", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/iceandfire/entity/EntityLightningDragon;distanceToSqr(DDD)D"))
    private double onGetDistance(EntityLightningDragon instance, double x, double y, double z) {
        return Math.sqrt(instance.distanceToSqr(x, y, z));
    }
}
