package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityIceDragon.class, remap = false)
public class EntityIceDragonMixin {
    @Redirect(method = "stimulateFire", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/iceandfire/entity/EntityIceDragon;distanceToSqr(DDD)D"))
    private double onGetDistance(EntityIceDragon instance, double x, double y, double z) {
        return Math.sqrt(instance.distanceToSqr(x, y, z));
    }
}
