package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityDragonBase.class, remap = false)
public class EntityDragonBaseMixin {
    @Shadow
    protected int fireTicks;

    @Redirect(method = "tryScorchTarget", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/iceandfire/entity/EntityDragonBase;stimulateFire(DDDI)V"))
    public void onStimulateFire(EntityDragonBase instance, double x, double y, double z, int i) {
        LivingEntity entity = instance.getTarget();
        assert entity != null;
        float distX = (float) (entity.getX() - instance.getX());
        float distZ = (float) (entity.getZ() - instance.getZ());
        int breathTicks = Mth.clamp(this.fireTicks, 0, 40);
        instance.stimulateFire(instance.getX() + distX * breathTicks / 40, entity.getY(), instance.getZ() + distZ * breathTicks / 40, 1);
    }
}
