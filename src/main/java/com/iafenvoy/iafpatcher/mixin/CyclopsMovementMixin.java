package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityCyclops;
//? >=1.17 {
import net.minecraft.world.phys.Vec3;
//?} else {
/*import net.minecraft.util.math.vector.Vector3d;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityCyclops.class, remap = false)
public class CyclopsMovementMixin {
    @Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = /*? >=1.17 {*/"Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"/*?} else {*//*"Lnet/minecraft/util/math/vector/Vector3d;scale(D)Lnet/minecraft/util/math/vector/Vector3d;"*//*?}*/, remap = true))
    private /*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/ iafpatcher$applySlowing(/*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/ movement, double factor) {
        /*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/ slowed = movement.scale(factor);
        ((EntityCyclops) (Object) this).setDeltaMovement(slowed);
        return slowed;
    }
}
