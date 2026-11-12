package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.iafenvoy.iafpatcher.config.IAFPConfig;
//? >=1.17 {
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
//?} else {
/*import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityDragonBase.class, remap = false)
public class DragonMealGrowthMixin {
    @Inject(method = "mobInteract", remap = true, at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/iceandfire/entity/EntityDragonBase;growDragon(I)V", remap = false))
    private void iafpatcher$resumeGrowth(/*? >=1.17 {*/Player/*?} else {*//*PlayerEntity*//*?}*/ player, /*? >=1.17 {*/InteractionHand/*?} else {*//*Hand*//*?}*/ hand, CallbackInfoReturnable</*? >=1.17 {*/InteractionResult/*?} else {*//*ActionResultType*//*?}*/> cir) {
        // This injection runs inside the original feeding branch, after its ownership checks.
        if (IAFPConfig.DRAGON_MEAL_RESTORES_GROWTH.get()
                && player.getItemInHand(hand).getItem() == IafItemRegistry.DRAGON_MEAL/*? >=1.17 {*/.get()/*?}*/) {
            ((EntityDragonBase) (Object) this).setAgingDisabled(false);
        }
    }
}
