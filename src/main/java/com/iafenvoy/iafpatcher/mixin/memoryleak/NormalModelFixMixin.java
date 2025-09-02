package com.iafenvoy.iafpatcher.mixin.memoryleak;

import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = {
        ModelAmphithere.class,
        ModelCockatrice.class,
        ModelCockatriceChick.class,
        ModelCyclops.class,
        ModelDeathWorm.class,
        ModelDreadBeast.class,
        ModelDreadScuttler.class,
        ModelGorgon.class,
        ModelHippocampus.class,
        ModelHippogryph.class,
        ModelHydraBody.class,
        ModelHydraHead.class,
        ModelSiren.class,
        ModelStymphalianBird.class,
        ModelTroll.class,
}, remap = false)
public class NormalModelFixMixin {
    @Shadow
    @Final
    private ModelAnimator animator;

    @Inject(method = "animate", at = @At("RETURN"))
    private void clearCache(CallbackInfo ci) {
        this.animator.update(null);
    }
}
