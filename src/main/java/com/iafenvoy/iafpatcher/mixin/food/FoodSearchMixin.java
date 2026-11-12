package com.iafenvoy.iafpatcher.mixin.food;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.PixieAIPickupItem;
import com.iafenvoy.iafpatcher.misc.FoodTags;
//? >=1.17 {
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
//?} else {
/*import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.item.ItemEntity;
*///?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = {
        //? <1.19 {
        /*AmphithereAITargetItems.class, CockatriceAITargetItems.class, HippogryphAITargetItems.class,
        *///?}
        PixieAIPickupItem.class}, remap = false)
public abstract class FoodSearchMixin extends TargetGoal {
    @Shadow(remap = false)
    @Final
    @Mutable
    protected Predicate<? super ItemEntity> targetEntitySelector;

    protected FoodSearchMixin(/*? >=1.17 {*/Mob/*?} else {*//*MobEntity*//*?}*/ mob, boolean checkSight, boolean nearby) {
        super(mob, checkSight, nearby);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void iafpatcher$useFoodTags(CallbackInfo ci) {
        if (this.mob instanceof EntityPixie) {
            EntityPixie pixie = (EntityPixie) this.mob;
            this.targetEntitySelector = item -> item != null && (pixie.isTame()
                    ? pixie.getHealth() < pixie.getMaxHealth() && FoodTags.HEAL_PIXIE.matches(item.getItem())
                    : FoodTags.TAME_PIXIE.matches(item.getItem()));
        } else {
            FoodTags food = this.mob instanceof EntityAmphithere ? FoodTags.HEAL_AMPITHERE
                    : this.mob instanceof EntityCockatrice ? FoodTags.HEAL_COCKATRICE : FoodTags.TAME_HIPPOGRYPH;
            this.targetEntitySelector = item -> item != null && food.matches(item.getItem());
        }
    }
}
