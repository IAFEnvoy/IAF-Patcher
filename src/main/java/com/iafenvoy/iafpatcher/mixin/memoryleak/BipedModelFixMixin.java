package com.iafenvoy.iafpatcher.mixin.memoryleak;

import com.github.alexthe666.iceandfire.client.model.ModelBipedBase;
import com.github.alexthe666.iceandfire.client.model.ModelDreadGhoul;
import com.github.alexthe666.iceandfire.client.model.ModelGhost;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = {
        ModelDreadGhoul.class,
        ModelGhost.class,
}, remap = false)
public abstract class BipedModelFixMixin<T extends LivingEntity> extends ModelBipedBase<T> {
    @Inject(method = "animate*", at = @At("RETURN"))
    private void clearCache(CallbackInfo ci) {
        this.animator.update(null);
    }
}
