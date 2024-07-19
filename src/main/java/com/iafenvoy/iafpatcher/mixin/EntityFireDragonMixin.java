package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityFireDragon.class, remap = false)
public class EntityFireDragonMixin {
    @Redirect(method = "stimulateFire", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/iceandfire/entity/EntityFireDragon;distanceToSqr(DDD)D",remap = true))
    private double onGetDistance(EntityFireDragon instance, double x, double y, double z) {
        return Math.sqrt(instance.distanceToSqr(x, y, z));
    }
}
