package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityMutlipartPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityMutlipartPart.class, remap = false)
public class MultipartCollisionMixin {
    @Redirect(method = "tick", remap = true, at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/iceandfire/entity/EntityMutlipartPart;collideWithNearbyEntities()V", ordinal = 0, remap = false))
    private void iafpatcher$skipDuplicateCollision(EntityMutlipartPart part) {
        // The common path below already performs this collision query for every part.
    }
}
