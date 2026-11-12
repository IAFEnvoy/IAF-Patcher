package com.iafenvoy.iafpatcher.mixin.ai;

import com.github.alexthe666.iceandfire.entity.ai.EntityAIWatchClosestIgnoreRider;
//? >=1.17 {
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
//?} else {
/*import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityAIWatchClosestIgnoreRider.class, remap = false)
public class WatchClosestIgnoreRiderMixin {
    @Shadow
    LivingEntity /*? >=1.17 {*/entity/*?} else {*//*mob*//*?}*/;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void iafpatcher$rememberObserver(/*? >=1.17 {*/Mob/*?} else {*//*MobEntity*//*?}*/ observer, Class<? extends LivingEntity> type, float distance, CallbackInfo ci) {
        this./*? >=1.17 {*/entity/*?} else {*//*mob*//*?}*/ = observer;
    }

    @Redirect(method = "canUse", remap = true, at = @At(value = "INVOKE", target = /*? >=1.17 {*/"Lcom/github/alexthe666/iceandfire/entity/ai/EntityAIWatchClosestIgnoreRider;isRidingOrBeingRiddenBy(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Z"/*?} else {*//*"Lcom/github/alexthe666/iceandfire/entity/ai/EntityAIWatchClosestIgnoreRider;isRidingOrBeingRiddenBy(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)Z"*//*?}*/, remap = false))
    private boolean iafpatcher$excludeRiders(Entity target, Entity observer) {
        return !EntityAIWatchClosestIgnoreRider.isRidingOrBeingRiddenBy(target, observer)
                && !EntityAIWatchClosestIgnoreRider.isRidingOrBeingRiddenBy(observer, target);
    }
}
